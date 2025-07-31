package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 역할: Order JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 주문 데이터의 실제 DB 접근을 담당
 */

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {



}
