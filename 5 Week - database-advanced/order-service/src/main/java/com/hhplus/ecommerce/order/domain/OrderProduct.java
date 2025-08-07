package com.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 역할: OrderProduct 도메인 엔티티 클래스 (MSA 버전)
 * 책임: 주문에서 참조할 상품 정보를 캡슐화 (외부 서비스 데이터)
 */

@Getter
@Setter
public class OrderProduct {

    private Long id;
    private Long productId;
    private String name;
    private BigDecimal price;

    public OrderProduct() {}

    public OrderProduct(Long productId, String name, BigDecimal price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

}







