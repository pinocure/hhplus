package com.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = coupons.stream()
                .map(OrderCoupon::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.subtract(discount);
    }

    // 주문 확인 (MSA 환경에서는 외부 서비스 호출로 재고 예약)
    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void pay() {
        this.status = "PAID";
    }

    public void fail() {
        this.status = "FAILED";
        rollback();
    }

    private void rollback() {
        coupons.forEach(coupon -> coupon.setUsed(false));       // 쿠폰 사용 취소
    }

}











