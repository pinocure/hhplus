package com.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 역할: Order 도메인 엔티티 클래스
 * 책임: 주문 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

@Getter
@Setter
public class Order {

    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private String status;                  // PENDING, CONFIRMED, PAID, FAILED
    private LocalDateTime createdAt;
    private List<OrderItem> items;
    private List<OrderCoupon> coupons;

    public Order(Long userId, List<OrderItem> items, List<OrderCoupon> coupons) {
        this.userId = userId;
        this.items = items;
        this.coupons = coupons;
        this.totalPrice = calculateTotalPrice();
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // 총 가격 계산 (쿠폰 할인 적용)
    private BigDecimal calculateTotalPrice() {
        BigDecimal sum = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = coupons.stream()
                .map(OrderCoupon::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.subtract(discount);
    }

    // 주문 확인 (재고 예약 호출)
    public void confirm() {
        items.forEach(item -> item.getProduct().reserveStock(item.getQuantity()));
        this.status = "CONFIRMED";
    }
}











