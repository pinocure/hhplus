package com.hhplus.ecommerce.coupon.application.port.in;

import com.hhplus.ecommerce.coupon.domain.Coupon;

import java.math.BigDecimal;

public interface CouponUseCase {

    Coupon issueCoupon(Long userId, Long eventId);
    void validateCoupon(String couponCode);

    BigDecimal getCouponDiscountAmount(String couponCode);

}
