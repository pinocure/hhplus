package com.hhplus.ecommerce.coupon.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponEventTest {

    @Test
    void issueCoupon_success() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        event.issueCoupon();
        assertEquals(9, event.getRemainingQuantity());
    }

    @Test
    void issueCoupon_noRemaining() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 0, LocalDateTime.now().plusDays(7));
        assertThrows(IllegalStateException.class, event::issueCoupon);
    }

    @Test
    void issueCoupon_expired() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().minusDays(1));
        assertThrows(IllegalStateException.class, event::issueCoupon);
    }

}










