package com.hhplus.ecommerce.order.application.saga.compensation;

import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import org.springframework.stereotype.Component;

/**
 * 역할: Coupon 서비스 보상 트랜잭션 처리기
 * 책임: 주문 실패 시 사용된 쿠폰을 원복하는 보상 트랜잭션을 실행
 */

@Component
public class CouponCompensation {

    private final CouponPort couponPort;

    public CouponCompensation(CouponPort couponPort) {
        this.couponPort = couponPort;
    }

    public void compensate(String couponCode) {
        try {
            couponPort.cancelCoupon(couponCode);
        } catch (Exception e) {
            throw new RuntimeException("Coupon 보상 실패 : ", e);
        }
    }

}










