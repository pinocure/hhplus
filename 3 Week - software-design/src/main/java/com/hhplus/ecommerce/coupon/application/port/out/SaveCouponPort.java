package com.hhplus.ecommerce.coupon.application.port.out;

import com.hhplus.ecommerce.coupon.domain.Coupon;

/**
 * 역할: coupon 저장 관련 출력 포트 인터페이스
 * 책임: 쿠폰 데이터 저장/업데이트 메서드를 정의하여 외부 어댑터가 이를 구현하도록 추상화
 */

public interface SaveCouponPort {

    Coupon saveCoupon(Coupon coupon);

}
