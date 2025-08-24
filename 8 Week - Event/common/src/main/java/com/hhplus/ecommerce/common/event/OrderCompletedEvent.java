package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final LocalDateTime orderedAt;


    public OrderCompletedEvent(Long orderId, Long userId, BigDecimal totalPrice, LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    @Override
    public String toString() {
        return "OrderCompletedEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", totalPrice=" + totalPrice +
                ", orderedAt=" + orderedAt +
                '}';
    }

}











