package com.hhplus.ecommerce.coupon.application.service;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
    public Coupon issueCoupon(Long userId, Long eventId) {
        int maxRetries = 5;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                return doIssueCoupon(userId, eventId);
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                retryCount++;

                if (retryCount >= maxRetries) {
                    throw new IllegalStateException("쿠폰 발급 중 충돌이 발생했습니다. 잠시 후 다시 시도해주세요.");
                }

                // 재시도 전 대기 (exponential backoff)
                try {
                    Thread.sleep(10 + (long)(Math.random() * 50 * retryCount));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("쿠폰 발급 중 인터럽트가 발생했습니다.", ie);
                }
            }
        }

        throw new IllegalStateException("쿠폰 발급에 실패했습니다.");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    protected Coupon doIssueCoupon(Long userId, Long eventId) {
        // 이벤트 조회
        CouponEvent event = couponRepository.findEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 이벤트가 존재하지 않습니다."));

        // 중복 발급 체크
        if (couponRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        // 쿠폰 발급 가능 여부 체크 및 수량 감소
        event.issueCoupon();

        // 이벤트 저장 (낙관적 락이 여기서 발생할 수 있음)
        couponRepository.saveEvent(event);

        // 쿠폰 생성
        Coupon coupon = new Coupon(
                UUID.randomUUID().toString(),
                userId,
                eventId,
                event.getDiscountAmount(),
                event.getExpiresAt()
        );

        // 쿠폰 저장
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

}













