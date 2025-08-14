package com.hhplus.ecommerce.coupon.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
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
                    throw new BusinessException(ErrorCode.LOCK_ERROR);
                }

                // 재시도 전 대기 (exponential backoff)
                try {
                    Thread.sleep(10 + (long)(Math.random() * 50 * retryCount));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(ErrorCode.LOCK_ERROR);
                }
            }
        }

        throw new BusinessException(ErrorCode.COUPON_FAIL);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    protected Coupon doIssueCoupon(Long userId, Long eventId) {
        // 이벤트 조회
        CouponEvent event = couponRepository.findEventById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_SOLD_OUT));

        // 중복 발급 체크
        if (couponRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_GET);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.isUsed()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.isUsed()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        return coupon.getDiscountAmount();
    }

}













