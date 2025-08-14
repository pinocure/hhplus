package com.hhplus.ecommerce.coupon.application.port.out;

import com.hhplus.ecommerce.coupon.adapter.out.persistence.CouponEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponEventJpaRepository extends JpaRepository<CouponEventJpaEntity, Long> {



}
