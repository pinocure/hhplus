package com.hhplus.ecommerce.coupon.application.saga;

import com.hhplus.ecommerce.common.event.CompensationEvent;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 역할: Coupon Saga 참여자
 * 책임: 분산 트랜잭션에서 Coupon 서비스의 역할을 수행하고 보상 트랜잭션을 처리
 */

@Component
public class CouponSagaParticipant {

    private final CouponRepository couponRepository;

    public CouponSagaParticipant(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @EventListener
    @Transactional
    public void handleCompensation(CompensationEvent event) {
        if ("COUPON_CANCEL".equals(event.getCompensationType())) {
            cancelCouponUsage(event);
        }
    }

    private void cancelCouponUsage(CompensationEvent event) {
        @SuppressWarnings("unchecked")
        var compensationData = (java.util.Map<String, Object>) event.getCompensationData();

        String couponCode = (String) compensationData.get("couponCode");

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다."));

        coupon.setUsed(false);
        couponRepository.save(coupon);
    }

}









