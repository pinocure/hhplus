package com.hhplus.ecommerce.product.adapter.out.persistence;

import com.hhplus.ecommerce.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer reservedStock;

    @Column(nullable = false)
    private Long version;

    public ProductJpaEntity(Long id, String name, BigDecimal price, Integer stock, Integer reservedStock, Long version) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.reservedStock = reservedStock;
        this.version = version;
    }

    public static ProductJpaEntity from(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getReservedStock(),
                product.getVersion()
        );
    }

    public Product toDomain() {
        return new Product(this.id, this.name, this.price, this.stock, this.reservedStock, this.version);
    }

}









