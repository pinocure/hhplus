package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.CouponFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


/**
 * 역할: Coupon 서비스와의 통신을 담당하는 Feign 기반 어댑터
 * 책임: Feign Client를 통해 Coupon 서비스의 기능을 호출
 */

@Component
@RequiredArgsConstructor
public class CouponFeignAdapter implements CouponPort {

    private final CouponFeignClient couponFeignClient;

    @Override
    public void validateCoupon(String couponCode) {
        try {
            couponFeignClient.validateCoupon(couponCode);
        } catch (FeignException e) {
            throw new RuntimeException("쿠폰 검증 실패 : " + e.getMessage());
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        try {
            return couponFeignClient.getCouponDiscountAmount(couponCode);
        } catch (FeignException e) {
            throw new RuntimeException("쿠폰 할인금액 조회 실패 : " + e.getMessage());
        }
    }
}









