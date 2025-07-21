package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
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
    private final BalanceUseCase balanceUseCase;

    public OrderService(OrderRepository orderRepository, ProductUseCase productUseCase, BalanceUseCase balanceUseCase) {
        this.orderRepository = orderRepository;
        this.productUseCase = productUseCase;
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

}













