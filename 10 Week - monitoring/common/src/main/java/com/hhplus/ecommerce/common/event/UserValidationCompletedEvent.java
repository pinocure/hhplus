package com.hhplus.ecommerce.common.event;

import java.time.LocalDateTime;

public class UserValidationCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final boolean success;
    private final String failureReason;
    private final LocalDateTime completedAt;

    public UserValidationCompletedEvent(Long orderId, Long userId, boolean success,
                                        String failureReason, LocalDateTime completedAt) {
        this.orderId = orderId;
        this.userId = userId;
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






