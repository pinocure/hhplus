package com.hhplus.ecommerce.coupon.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.common.lock.DistributedLock;
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
    @Transactional
    @DistributedLock(key = "lock:coupon:event:#={eventId}:user:#={userId}", waitTime = 3, leaseTime = 2)
    public Coupon issueCoupon(Long userId, Long eventId) {
        CouponEvent event = couponRepository.findEventById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_SOLD_OUT));

        if (couponRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        event.issueCoupon();

        couponRepository.saveEvent(event);

        Coupon coupon = new Coupon(
                UUID.randomUUID().toString(),
                userId,
                eventId,
                event.getDiscountAmount(),
                event.getExpiresAt()
        );

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













