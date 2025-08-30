package com.hhplus.ecommerce.order.adapter.out.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;


@FeignClient(name = "coupon-service", url = "${services.coupon.url}")
public interface CouponFeignClient {

    @PostMapping("/coupons/validate")
    void validateCoupon(@RequestParam("couponCode") String couponCode);

    @GetMapping("/coupons/discount")
    BigDecimal getCouponDiscountAmount(@RequestParam("couponCode") String couponCode);
}
