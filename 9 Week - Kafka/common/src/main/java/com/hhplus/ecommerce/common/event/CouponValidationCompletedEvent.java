package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CouponValidationCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final boolean success;
    private final String failureReason;
    private final List<CouponInfo> validCoupons;
    private final BigDecimal totalDiscount;
    private final LocalDateTime completedAt;

    public CouponValidationCompletedEvent(Long orderId, Long userId, boolean success,
                                          String failureReason, List<CouponInfo> validCoupons,
                                          BigDecimal totalDiscount, LocalDateTime completedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.success = success;
        this.failureReason = failureReason;
        this.validCoupons = validCoupons;
        this.totalDiscount = totalDiscount;
        this.completedAt = completedAt;
    }


    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public List<CouponInfo> getValidCoupons() {
        return validCoupons;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}








