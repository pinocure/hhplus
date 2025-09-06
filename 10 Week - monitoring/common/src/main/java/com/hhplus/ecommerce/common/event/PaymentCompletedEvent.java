package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final BigDecimal amount;
    private final boolean success;
    private final String failureReason;
    private final LocalDateTime completedAt;

    public PaymentCompletedEvent(Long orderId, Long userId, BigDecimal amount,
                                 boolean success, String failureReason, LocalDateTime completedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.success = success;
        this.failureReason = failureReason;
        this.completedAt = completedAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}











