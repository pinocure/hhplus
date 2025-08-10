package com.hhplus.ecommerce.coupon.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 역할: couponEvent 도메인 엔티티 클래스
 * 책임: 쿠폰 이벤트 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

@Getter
@Setter
public class CouponEvent {

    private Long id;
    private String name;
    private BigDecimal discountAmount;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private LocalDateTime expiresAt;
    private Long version;

    public CouponEvent(Long id, String name, BigDecimal discountAmount, Integer totalQuantity, LocalDateTime expiresAt) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
        this.expiresAt = expiresAt;
        this.version = null;
    }

    public void issueCoupon() {
        if (remainingQuantity <= 0) {
            throw new IllegalStateException("쿠폰이 모두 발급되었습니다.");
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new IllegalStateException("쿠폰 이벤트가 만료되었습니다.");
        }
        this.remainingQuantity--;
    }

}













