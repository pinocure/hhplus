package com.hhplus.ecommerce.order.domain;

import com.hhplus.ecommerce.coupon.domain.Coupon;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 역할: OrderCoupon 도메인 엔티티 클래스
 * 책임: 주문 쿠폰 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

@Getter
@Setter
public class OrderCoupon {

    private Long id;
    private Long orderId;
    private String couponCode;
    private BigDecimal discountAmount;

    public OrderCoupon(String couponCode, BigDecimal discountAmount) {
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
    }

}










