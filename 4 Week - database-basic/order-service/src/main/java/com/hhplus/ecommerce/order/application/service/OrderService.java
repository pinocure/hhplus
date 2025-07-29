package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.order.adapter.out.external.ProductClientAdapter;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 역할: order 서비스 구현 클래스
 * 책임: OrderUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용.
 *      외부 서비스와 HTTP 통신을 통해 비즈니스 로직 처리
 */

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final BalancePort balancePort;

    public OrderService(OrderRepository orderRepository, ProductPort productPort, CouponPort couponPort, BalancePort balancePort) {
        this.orderRepository = orderRepository;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.balancePort = balancePort;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();

        IntStream.range(0, productIds.size()).forEach(i -> {
            ProductClientAdapter.ProductDto productDto = productPort.getProduct(productIds.get(i));
            if (productDto.getStock() < quantities.get(i)) {
                throw new IllegalArgumentException(productDto.getName() + "의 재고가 부족합니다.");
            }

            OrderProduct orderProduct = new OrderProduct(
                    productDto.getId(),
                    productDto.getName(),
                    productDto.getPrice()
            );

            OrderProduct savedOrderProduct = orderRepository.saveOrderProduct(orderProduct);

            OrderItem orderItem = new OrderItem(
                    savedOrderProduct.getId(),
                    quantities.get(i),
                    productDto.getPrice()
            );

            items.add(orderItem);
        });

        // 실제 쿠폰 할인 처리
        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(code -> {
            couponPort.validateCoupon(code);
            BigDecimal discountAmount = couponPort.getCouponDiscountAmount(code);
            coupons.add(new OrderCoupon(code, discountAmount));
        });

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
            balancePort.deductBalance(order.getUserId(), totalPrice);

            order.getItems().forEach(item -> {
                OrderProduct orderProduct = orderRepository.findOrderProductById(item.getOrderProductId())
                                .orElseThrow(() -> new RuntimeException("주문 상품 정보를 찾을 수 없습니다."));
                productPort.deductStock(orderProduct.getProductId(), item.getQuantity());
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













