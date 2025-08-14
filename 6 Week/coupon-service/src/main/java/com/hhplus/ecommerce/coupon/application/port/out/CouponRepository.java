package com.hhplus.ecommerce.coupon.application.port.out;

import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findByCode(String code);
    Coupon save(Coupon coupon);

    Optional<CouponEvent> findEventById(Long eventId);
    CouponEvent saveEvent(CouponEvent event);

    Optional<Coupon> findByUserIdAndEventId(Long userId, Long eventId);

}
