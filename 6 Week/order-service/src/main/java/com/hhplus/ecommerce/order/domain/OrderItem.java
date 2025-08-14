package com.hhplus.ecommerce.order.domain;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long orderProductId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItem(Long orderProductId, Integer quantity, BigDecimal unitPrice) {
        this.orderProductId = orderProductId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

}











