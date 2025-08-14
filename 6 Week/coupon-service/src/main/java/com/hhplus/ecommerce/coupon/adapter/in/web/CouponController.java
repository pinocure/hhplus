package com.hhplus.ecommerce.coupon.adapter.in.web;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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











