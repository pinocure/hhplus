package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 역할: order 서비스 구현 클래스
 * 책임: OrderUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용.
 */

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductUseCase productUseCase;
    private final CouponUseCase couponUseCase;
    private final BalanceUseCase balanceUseCase;

    public OrderService(OrderRepository orderRepository, ProductUseCase productUseCase, CouponUseCase couponUseCase, BalanceUseCase balanceUseCase) {
        this.orderRepository = orderRepository;
        this.productUseCase = productUseCase;
        this.couponUseCase = couponUseCase;
        this.balanceUseCase = balanceUseCase;
    }


    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();
        IntStream.range(0, productIds.size()).forEach(i -> {
            Product product = productUseCase.getProduct(productIds.get(i));
            if (!product.hasEnoughStock(quantities.get(i))) {
                throw new IllegalArgumentException(product.getName() + "의 재고가 부족합니다.");
            }
            items.add(new OrderItem(product, quantities.get(i)));
        });

        // MOCK Discount
        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(code -> coupons.add(new OrderCoupon(code, BigDecimal.ZERO)));

        Order order = new Order(userId, items, coupons);
        order.confirm();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. 관리자에게 문의해주세요."));
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }

        try {
            BigDecimal totalPrice = order.getTotalPrice();
            balanceUseCase.deductBalance(order.getUserId(), totalPrice);

            order.getItems().forEach(item -> {
                productUseCase.deductStock(item.getProduct().getId(), item.getQuantity());
            });

            order.getCoupons().forEach(coupon -> coupon.setUsed(true));

            order.pay();
        } catch (Exception e) {
            order.fail();
            throw new RuntimeException("결제 실패: " + e.getMessage());
        }

        return orderRepository.save(order);
    }

}













