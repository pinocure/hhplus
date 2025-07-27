package com.hhplus.ecommerce.coupon.application.service;

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
 * 책임: CouponUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용
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

        // optimistic lock - In Memory
        if (!couponRepository.checkEventVersion(event.getId(), event.getVersion())) {
            throw new IllegalStateException("쿠폰 발급 중 충돌이 발생하였습니다. 관리자에게 문의해주세요.");
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
        Coupon saved = couponRepository.save(coupon);

        return saved;
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

}













