package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.domain.OrderProduct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    public OrderProductJpaEntity(Long id, Long productId, String name, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public static OrderProductJpaEntity from(OrderProduct orderProduct) {
        return new OrderProductJpaEntity(
                orderProduct.getId(),
                orderProduct.getProductId(),
                orderProduct.getName(),
                orderProduct.getPrice()
        );
    }

    public OrderProduct toDomain() {
        OrderProduct product = new OrderProduct(this.productId, this.name, this.price);
        product.setId(this.id);
        return product;
    }

}










