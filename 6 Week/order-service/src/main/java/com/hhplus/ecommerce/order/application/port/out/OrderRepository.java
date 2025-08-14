package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;

import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(long orderId);

    // 비관적 락을 사용하여 주문 조회 - 주문 결제 처리 시 동시성 제어를 위해 사용
    Optional<Order> findByIdWithPessimisticLock(long orderId);

    // 낙관적 락을 사용하여 주문 조회 - 일반적인 주문 조회 시 사용
    Optional<Order> findByIdWithOptimisticLock(long orderId);

    Order save(Order order);

    OrderProduct saveOrderProduct(OrderProduct orderProduct);
    Optional<OrderProduct> findOrderProductById(long orderProductId);

}
