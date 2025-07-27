package com.hhplus.ecommerce.order.adapter.out.external;

import com.hhplus.ecommerce.order.application.port.out.CouponPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 역할: Coupon 서비스와의 통신을 담당하는 외부 어댑터
 * 책임: HTTP 통신을 통해 Coupon 서비스의 기능을 호출
 */

@Component
public class CouponClientAdapter implements CouponPort {

    private final RestTemplate restTemplate;
    private final String couponServiceUrl;

    public CouponClientAdapter(RestTemplate restTemplate,
                               @Value("${services.coupon.url}") String couponServiceUrl) {
        this.restTemplate = restTemplate;
        this.couponServiceUrl = couponServiceUrl;
    }

    @Override
    public void validateCoupon(String couponCode) {
        try {
            restTemplate.postForObject(couponServiceUrl + "/coupons/validate?couponCode=" + couponCode, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 검증 실패 : " + e.getMessage());
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        try {
            return restTemplate.getForObject(couponServiceUrl + "/coupons/discount?couponCode=" + couponCode, BigDecimal.class);
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 할인금액 조회 실패 : " + e.getMessage());
        }
    }

}







