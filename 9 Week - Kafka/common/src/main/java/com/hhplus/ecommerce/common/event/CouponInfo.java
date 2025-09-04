package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;

public class CouponInfo {

    private final String code;
    private final BigDecimal discountAmount;

    public CouponInfo(String code, BigDecimal discountAmount) {
        this.code = code;
        this.discountAmount = discountAmount;
    }


    public String getCode() {
        return code;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

}







