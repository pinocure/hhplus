package com.hhplus.ecommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OrderCouponTest {

    @Test
    void createOrderCoupon_success() {
        OrderCoupon coupon = new OrderCoupon("CODE", BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), coupon.getDiscountAmount());
        assertFalse(coupon.isUsed());
    }

}









