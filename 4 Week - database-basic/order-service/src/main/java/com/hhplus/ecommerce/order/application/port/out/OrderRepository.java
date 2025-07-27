package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.domain.Order;

import java.util.Optional;

/**
 * 역할: order 리포지토리 인터페이스 (out port)
 * 책임: Order 데이터 접근을 추상화하여 도메인과 외부 저장소 간 의존성을 분리
 */

public interface OrderRepository {

    Optional<Order> findById(long orderId);
    Order save(Order order);

}
