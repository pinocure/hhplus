package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 역할: CouponEvent JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 쿠폰 이벤트 데이터의 실제 DB 접근을 담당
 */

public interface CouponEventJpaRepository extends JpaRepository<CouponEventJpaEntity, Long> {



}
