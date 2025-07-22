package com.hhplus.ecommerce.coupon.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {

    @Test
    void useCoupon_success() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        coupon.use();
        assertTrue(coupon.isUsed());
    }

    @Test
    void useCoupon_alreadyUsed() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        coupon.setUsed(true);
        assertThrows(IllegalStateException.class, coupon::use);
    }

    @Test
    void useCoupon_expired() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        assertThrows(IllegalStateException.class, coupon::use);
    }

}










