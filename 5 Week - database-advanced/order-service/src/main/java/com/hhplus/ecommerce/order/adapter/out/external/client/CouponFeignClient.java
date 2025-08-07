package com.hhplus.ecommerce.order.adapter.out.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 역할: Coupon 서비스의 REST API를 호출하기 위한 Feign Client 인터페이스
 * 책임: HTTP 통신 세부사항을 선언적으로 정의하고 Coupon 서비스의 엔드포인트와 매핑
 */

@FeignClient(name = "coupon-service", url = "${services.coupon.url}")
public interface CouponFeignClient {

    @PostMapping("/coupons/validate")
    void validateCoupon(@RequestParam("couponCode") String couponCode);

    @GetMapping("/coupons/discount")
    BigDecimal getCouponDiscountAmount(@RequestParam("couponCode") String couponCode);

    @PostMapping("/coupons/cancel")
    void cancelCoupon(@RequestParam("couponCode") String couponCode);

}
