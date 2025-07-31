package com.hhplus.ecommerce.order.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 역할: OrderItem JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 주문 항목 데이터의 실제 DB 접근을 담당
 */

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    List<OrderItemJpaEntity> findByOrderId(Long orderId);

}
