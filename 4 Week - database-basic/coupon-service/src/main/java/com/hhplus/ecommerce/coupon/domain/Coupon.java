package com.hhplus.ecommerce.coupon.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 역할: coupon 도메인 엔티티 클래스
 * 책임: 쿠폰 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
 */

@Getter
@Setter
public class Coupon {

    private String code;
    private Long userId;
    private Long couponEventId;
    private BigDecimal discountAmount;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private boolean used;

    public Coupon(String code, Long userId, Long couponEventId, BigDecimal discountAmount, LocalDateTime expiresAt) {
        this.code = code;
        this.userId = userId;
        this.couponEventId = couponEventId;
        this.discountAmount = discountAmount;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public void use() {
        if (used) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }
        this.used = true;
    }

}













