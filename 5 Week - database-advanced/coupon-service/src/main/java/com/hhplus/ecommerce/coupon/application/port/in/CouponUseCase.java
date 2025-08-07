package com.hhplus.ecommerce.coupon.application.port.in;

import com.hhplus.ecommerce.coupon.domain.Coupon;

import java.math.BigDecimal;

/**
 * 역할: coupon 도메인의 입력 포트 인터페이스
 * 책임: 쿠폰 관련 유즈케이스(발급, 검증, 할인금액 조회)를 정의하여 애플리케이션 서비스가 이를 구현하도록 함
 */

public interface CouponUseCase {

    Coupon issueCoupon(Long userId, Long eventId);
    void validateCoupon(String couponCode);

    BigDecimal getCouponDiscountAmount(String couponCode);

}
