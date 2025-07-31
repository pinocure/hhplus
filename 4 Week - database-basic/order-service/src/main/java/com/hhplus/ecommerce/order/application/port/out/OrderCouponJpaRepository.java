package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderCouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 역할: OrderCoupon JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 주문 쿠폰 데이터의 실제 DB 접근을 담당
 */

public interface OrderCouponJpaRepository extends JpaRepository<OrderCouponJpaEntity, Long> {

    List<OrderCouponJpaEntity> findByOrderId(Long orderId);

}
