package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 역할: order 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: OrderRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화
 */

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    // In-Memory 구현 -> JPA로 변경해야함
    private final Map<Long, Order> orderStore  = new HashMap<>();
    private final Map<Long, OrderProduct> orderProductStore  = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final AtomicLong orderProductIdGenerator = new AtomicLong(1);


    @Override
    public Optional<Order> findById(long orderId) {
        return Optional.ofNullable(orderStore.get(orderId));
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orderStore.put(order.getId(), order);

        return order;
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        if (orderProduct.getId() == null) {
            orderProduct.setId(orderProductIdGenerator.getAndIncrement());
        }
        orderProductStore.put(orderProduct.getId(), orderProduct);
        return orderProduct;
    }

    @Override
    public Optional<OrderProduct> findOrderProductById(long orderProductId) {
        return Optional.ofNullable(orderProductStore.get(orderProductId));
    }

}











