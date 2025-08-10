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
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderCouponJpaRepository orderCouponJpaRepository;
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
    public Optional<Order> findByIdWithPessimisticLock(long orderId) {
        return orderJpaRepository.findByIdWithPessimisticLock(orderId)
                .map(entity -> {
                    Order order = entity.toDomain();
                    // 연관된 items와 coupons 로드
                    order.setItems(orderItemJpaRepository.findByOrderId(orderId).stream()
                            .map(OrderItemJpaEntity::toDomain)
                            .toList());
                    order.setCoupons(orderCouponJpaRepository.findByOrderId(orderId).stream()
                            .map(OrderCouponJpaEntity::toDomain)
                            .toList());
                    return order;
                });
    }

    @Override
    public Optional<Order> findByIdWithOptimisticLock(long orderId) {
        return orderJpaRepository.findByIdWithOptimisticLock(orderId)
                .map(entity -> {
                    Order order = entity.toDomain();
                    // 연관된 items와 coupons 로드
                    order.setItems(orderItemJpaRepository.findByOrderId(orderId).stream()
                            .map(OrderItemJpaEntity::toDomain)
                            .toList());
                    order.setCoupons(orderCouponJpaRepository.findByOrderId(orderId).stream()
                            .map(OrderCouponJpaEntity::toDomain)
                            .toList());
                    return order;
                });
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.from(order);
        OrderJpaEntity saved = orderJpaRepository.save(entity);

        Order result = saved.toDomain();

        // 기존 items와 coupons 저장 로직
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().forEach(item -> {
                item.setOrderId(saved.getId());
                OrderItemJpaEntity itemEntity = OrderItemJpaEntity.from(item);
                orderItemJpaRepository.save(itemEntity);
            });
        }

        if (order.getCoupons() != null && !order.getCoupons().isEmpty()) {
            order.getCoupons().forEach(coupon -> {
                coupon.setOrderId(saved.getId());
                OrderCouponJpaEntity couponEntity = OrderCouponJpaEntity.from(coupon);
                orderCouponJpaRepository.save(couponEntity);
            });
        }

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











