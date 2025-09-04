package com.hhplus.ecommerce.common.event;

import java.math.BigDecimal;

public class OrderItemInfo {

    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;

    public OrderItemInfo(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

}









