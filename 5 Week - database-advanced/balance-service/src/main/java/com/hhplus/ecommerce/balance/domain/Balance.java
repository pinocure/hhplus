package com.hhplus.ecommerce.balance.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 역할: balance 도메인 엔티티 클래스
 * 책임: 잔액 속성과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지 (user 엔티티와 연계)
 */

@Getter
@Setter
public class Balance {

    private Long userId;            // 사용자 ID
    private BigDecimal amount;      // 잔액

    public Balance(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // 충전
    public void charge(BigDecimal chargeAmount) {
        if (chargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 1원 이상이어야 합니다.");
        }
        this.amount = this.amount.add(chargeAmount);
    }

    // 차감
    public void deduct(BigDecimal deductAmount) {
        if (deductAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("차감 금액은 1원 이상이어야 합니다.");
        }
        if (this.amount.compareTo(deductAmount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount = this.amount.subtract(deductAmount);
    }

}













