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
    private String name;
    private BigDecimal price;

    public OrderProduct(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public boolean hasEnoughStock(int quantity) {
        // MSA 환경에서는 실제 재고 확인은 Product 서비스에서 처리
        return true;
    }

    public void reserveStock(int quantity) {
        // MSA 환경에서는 실제 재고 예약은 Product 서비스에서 처리
    }

    public void rollbackReservedStock(int quantity) {
        // MSA 환경에서는 실제 재고 롤백은 Product 서비스에서 처리
    }

}







