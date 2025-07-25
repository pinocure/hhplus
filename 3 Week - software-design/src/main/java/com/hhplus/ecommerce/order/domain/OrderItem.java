package com.hhplus.ecommerce.order.domain;

import com.hhplus.ecommerce.product.domain.Product;
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
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

}











