package com.hhplus.ecommerce.coupon.application.port.out;

import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;

import java.util.Optional;

/**
 * 역할: coupon 리포지토리 인터페이스 (out port)
 * 책임: Coupon 데이터 접근을 추상화하여 도메인과 외부 저장소 간 의존성을 분리
 */

public interface CouponRepository {

    Optional<Coupon> findByCode(String code);
    Coupon save(Coupon coupon);

    Optional<CouponEvent> findEventById(Long eventId);
    CouponEvent saveEvent(CouponEvent event);

    Optional<Coupon> findByUserIdAndEventId(Long userId, Long eventId);

}
