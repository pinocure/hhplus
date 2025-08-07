package com.hhplus.ecommerce.order.domain;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 역할: OrderItem 도메인 엔티티 클래스
 * 책임: 주문 항목 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

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











