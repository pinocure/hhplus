package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long orderProductId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    public OrderItemJpaEntity(Long id, Long orderId, Long orderProductId, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.orderProductId = orderProductId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItemJpaEntity from(OrderItem orderItem) {
        return new OrderItemJpaEntity(
                orderItem.getId(),
                orderItem.getOrderId(),
                orderItem.getOrderProductId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice()
        );
    }

    public OrderItem toDomain() {
        OrderItem item = new OrderItem(this.orderProductId, this.quantity, this.unitPrice);
        item.setId(this.id);
        item.setOrderId(this.orderId);
        return item;
    }

}










