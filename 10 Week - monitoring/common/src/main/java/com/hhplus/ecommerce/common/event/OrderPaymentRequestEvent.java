package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderPaymentRequestEvent {

    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalAmount;
    private final List<OrderItemInfo> items;
    private final List<String> couponCodes;
    private final LocalDateTime requestedAt;

    public OrderPaymentRequestEvent(Long orderId, Long userId, BigDecimal totalAmount,
                                    List<OrderItemInfo> items, List<String> couponCodes, LocalDateTime requestedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.couponCodes = couponCodes;
        this.requestedAt = requestedAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItemInfo> getItems() {
        return items;
    }

    public List<String> getCouponCodes() {
        return couponCodes;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}











