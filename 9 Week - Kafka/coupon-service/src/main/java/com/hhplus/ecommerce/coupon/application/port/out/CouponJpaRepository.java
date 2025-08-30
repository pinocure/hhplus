package com.hhplus.ecommerce.coupon.application.port.out;

import com.hhplus.ecommerce.coupon.adapter.out.persistence.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, String> {

    Optional<CouponJpaEntity> findByUserIdAndCouponEventId(Long userId, Long couponEventId);

}
