package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.CouponFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 역할: CouponPort의 구현체로서 Feign Client를 사용한 Coupon 서비스 통신 어댑터
 * 책임: Feign Client 호출, 예외 처리, 에러 메시지 변환 등 기술적 세부사항을 처리
 */

@Component
public class CouponFeignAdapter implements CouponPort {

    private final CouponFeignClient couponFeignClient;

    public CouponFeignAdapter(CouponFeignClient couponFeignClient) {
        this.couponFeignClient = couponFeignClient;
    }


    @Override
    public void validateCoupon(String couponCode) {
        try {
            couponFeignClient.validateCoupon(couponCode);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 검증 실패 : " + e.getMessage());
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        try {
            return couponFeignClient.getCouponDiscountAmount(couponCode);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 할인금액 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void cancelCoupon(String couponCode) {
        try {
            couponFeignClient.cancelCoupon(couponCode);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 취소 실패 : " + e.getMessage());
        }
    }

    @Override
    public void useCoupon(String couponCode) {
        try {
            couponFeignClient.useCoupon(couponCode);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 사용 실패 : " + e.getMessage());
        }
    }

}









