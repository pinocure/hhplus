package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 역할: Coupon JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 쿠폰 데이터의 실제 DB 접근을 담당
 */

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, String> {

    Optional<CouponJpaEntity> findByUserIdAndCouponEventId(Long userId, Long couponEventId);

}
