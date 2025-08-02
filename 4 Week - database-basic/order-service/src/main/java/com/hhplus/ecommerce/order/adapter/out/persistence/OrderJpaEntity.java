package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 역할: Order JPA 엔티티 클래스
 * 책임: 주문 테이블과 매핑되는 JPA 엔티티로, DB 스키마와 도메인 모델 간의 변환을 담당
 */

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public OrderJpaEntity(Long id, Long userId, BigDecimal totalPrice, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static OrderJpaEntity from(Order order) {
        return new OrderJpaEntity(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public Order toDomain() {
        Order order = new Order(this.userId, new ArrayList<>(), new ArrayList<>());
        order.setId(this.id);
        order.setTotalPrice(this.totalPrice);
        order.setStatus(this.status);
        order.setCreatedAt(this.createdAt);
        return order;
    }

}










