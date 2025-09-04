package com.hhplus.ecommerce.common.event;

import java.time.LocalDateTime;

public class UserValidationRequestEvent {

    private final Long orderId;
    private final Long userId;
    private final LocalDateTime requestedAt;

    public UserValidationRequestEvent(Long orderId, Long userId, LocalDateTime requestedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.requestedAt = requestedAt;
    }


    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

}








