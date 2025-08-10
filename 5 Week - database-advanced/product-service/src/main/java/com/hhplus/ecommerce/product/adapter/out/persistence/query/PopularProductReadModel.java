package com.hhplus.ecommerce.product.adapter.out.persistence.query;

import jakarta.persistence.Column;
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
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "sales_count", nullable = false)
    private Integer salesCount;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public PopularProductReadModel(Long productId, String name, BigDecimal price, Integer stock, Integer salesCount, LocalDateTime lastUpdated) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.salesCount = salesCount != null ? salesCount : 0;
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }

    public void increaseSalesCount(int quantity) {
        this.salesCount += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public void updateStock(int newStock) {
        this.stock = newStock;
        this.lastUpdated = LocalDateTime.now();
    }

}









