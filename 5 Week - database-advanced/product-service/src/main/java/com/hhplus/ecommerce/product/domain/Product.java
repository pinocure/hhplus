package com.hhplus.ecommerce.product.domain;

import com.hhplus.ecommerce.product.domain.event.ProductSoldEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 역할: product 도메인 엔티티 클래스
 * 책임: 상품 속성(가격, 재고 등)과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

@Getter
@Setter
public class Product {

    private Long id;                    // 사용자 ID
    private String name;                // 사용자 이름
    private BigDecimal price;           // 가격
    private Integer stock;              // 재고
    private Integer reservedStock;      // 예약 재고 (주문 대기)
    private Long version;               // 버전관리 (Lock 관련)

    private transient ApplicationEventPublisher eventPublisher;

    public Product(Long id, String name, BigDecimal price, Integer stock, Integer reservedStock, Long version) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.reservedStock = reservedStock != null ? reservedStock : 0;
        this.version = version != null ? version : 0L;
    }

    // 재고 확인
    public boolean hasEnoughStock(int quantity) {
        return this.stock >= quantity;
    }

    // 재고 예약
    public void reserveStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.reservedStock += quantity;
        this.version++;
    }

    public void deductStock(int quantity) {
        if (this.reservedStock < quantity) {
            throw new IllegalArgumentException("예약 재고가 부족합니다.");
        }
        this.stock -= quantity;
        this.reservedStock -= quantity;
        this.version++;
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new ProductSoldEvent(id, name, price, stock, quantity, LocalDateTime.now()));
        }
    }

    public void rollbackReservedStock(int quantity) {
        this.reservedStock -= quantity;
        this.version++;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

}












