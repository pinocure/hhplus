package com.hhplus.ecommerce.common.event;

public class StockInsufficientEvent {

    private final Long orderId;
    private final Long productId;
    private final String productName;
    private final Integer requestedQuantity;
    private final Integer availableStock;

    public StockInsufficientEvent(Long orderId, Long productId, String productName,
                                  Integer requestedQuantity, Integer availableStock) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }
}









