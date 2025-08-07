package com.hhplus.ecommerce.coupon.application.service;

import com.hhplus.ecommerce.common.lock.OptimisticLockException;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 역할: coupon 서비스 구현 클래스
 * 책임: CouponUseCase를 구현하며, 낙관적 락을 통한 동시성 제어와 함께 유즈케이스 흐름을 조율
 */

@Service
public class CouponService implements CouponUseCase {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }


    @Override
    @Transactional
    public Coupon issueCoupon(Long userId, Long eventId) {
        CouponEvent event = couponRepository.findEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 이벤트가 존재하지 않습니다."));

        if (couponRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        event.issueCoupon();
        Coupon coupon = new Coupon(
                UUID.randomUUID().toString(),
                userId,
                eventId,
                event.getDiscountAmount(),
                event.getExpiresAt()
        );

        couponRepository.saveEvent(event);
        return couponRepository.save(coupon);
    }

    @Override
    public void validateCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // 쿠폰 유효성 검증
        if (coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        return coupon.getDiscountAmount();
    }

    @Transactional
    public void cancelCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        coupon.setUsed(false);
        couponRepository.save(coupon);
    }

}













