package com.hhplus.ecommerce.order.application.service;

import java.math.BigDecimal;

public class OrderProcessingStatus {

    private boolean userValidated = false;
    private boolean couponValidated = false;
    private boolean paymentCompleted = false;
    private boolean stockSufficient = true;             // 기본값 true, 문제시 false
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    public boolean isAllCompleted() {
        return userValidated && couponValidated && paymentCompleted;
    }

    public boolean isAllSuccessful() {
        return userValidated && couponValidated && paymentCompleted && stockSufficient;
    }


    public void setUserValidated(boolean userValidated) {
        this.userValidated = userValidated;
    }

    public void setCouponValidated(boolean couponValidated) {
        this.couponValidated = couponValidated;
    }

    public void setPaymentCompleted(boolean paymentCompleted) {
        this.paymentCompleted = paymentCompleted;
    }

    public void setStockSufficient(boolean stockSufficient) {
        this.stockSufficient = stockSufficient;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Override
    public String toString() {
        return String.format("OrderProcessingStatus{userValidated=%s, couponValidated=%s, paymentCompleted=%s, stockSufficient=%s}",
                userValidated, couponValidated, paymentCompleted, stockSufficient);
    }

}










