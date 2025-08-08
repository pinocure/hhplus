package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.application.port.out.*;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 역할: order 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: OrderRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화 및 도메인-JPA 엔티티 간 변환
 */

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderProductJpaRepository orderProductJpaRepository;


    @Override
    public Optional<Order> findById(long orderId) {
        return orderJpaRepository.findById(orderId)
                .map(entity -> {
                    Order order = entity.toDomain();

                    return order;
                });
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.from(order);
        OrderJpaEntity saved = orderJpaRepository.save(entity);

        Order result = saved.toDomain();

        result.setItems(order.getItems() != null ? order.getItems() : new ArrayList<>());
        result.setCoupons(order.getCoupons() != null ? order.getCoupons() : new ArrayList<>());
        return result;
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        OrderProductJpaEntity entity = OrderProductJpaEntity.from(orderProduct);
        OrderProductJpaEntity saved = orderProductJpaRepository.save(entity);

        return saved.toDomain();
    }

    @Override
    public Optional<OrderProduct> findOrderProductById(long orderProductId) {
        return orderProductJpaRepository.findById(orderProductId)
                .map(OrderProductJpaEntity::toDomain);
    }

}











