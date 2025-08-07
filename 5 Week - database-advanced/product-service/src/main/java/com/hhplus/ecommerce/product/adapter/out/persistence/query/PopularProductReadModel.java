package com.hhplus.ecommerce.product.adapter.out.persistence.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 역할: 인기 상품 조회 전용 읽기 모델 (CQRS Read Model)
 * 책임: 이벤트 소싱을 통해 동기화되는 읽기 최적화 뷰를 제공하여 빠른 조회 성능 보장
 */

@Entity
@Table(name = "popular_product_view")
@Getter
@NoArgsConstructor
public class PopularProductReadModel {

    @Id
    private Long productId;

    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer salesCount;
    private LocalDateTime lastUpdated;

    public PopularProductReadModel(Long productId, String name, BigDecimal price, Integer stock, Integer salesCount, LocalDateTime lastUpdated) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.salesCount = salesCount;
        this.lastUpdated = lastUpdated;
    }

    public void increaseSalesCount(int quantity) {
        this.salesCount += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

}









