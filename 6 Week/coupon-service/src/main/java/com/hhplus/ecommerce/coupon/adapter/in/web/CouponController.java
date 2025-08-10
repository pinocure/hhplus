package com.hhplus.ecommerce.coupon.adapter.in.web;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 역할: coupon 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: 쿠폰 관련 HTTP 요청을 처리하고, CouponUseCase를 호출하여 유즈케이스 흐름을 트리거
 */

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponUseCase couponUseCase;

    public CouponController(final CouponUseCase couponUseCase) {
        this.couponUseCase = couponUseCase;
    }

    @PostMapping("/issue")
    public ResponseEntity<Coupon> issueCoupon(@RequestParam Long userId, @RequestParam Long eventId) {
        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);
        return ResponseEntity.ok(coupon);
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateCoupon(@RequestParam String couponCode) {
        couponUseCase.validateCoupon(couponCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/discount")
    public ResponseEntity<BigDecimal> getCouponDiscountAmount(@RequestParam String couponCode) {
        BigDecimal discountAmount = couponUseCase.getCouponDiscountAmount(couponCode);
        return ResponseEntity.ok(discountAmount);
    }

}











