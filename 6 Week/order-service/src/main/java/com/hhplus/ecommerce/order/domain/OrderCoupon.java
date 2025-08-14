package com.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderCoupon {

    private Long id;
    private Long orderId;
    private String couponCode;
    private BigDecimal discountAmount;
    private boolean used;

    public OrderCoupon(String couponCode, BigDecimal discountAmount) {
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.used = false;
    }

}










