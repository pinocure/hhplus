package com.hhplus.ecommerce.balance.domain;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.amount = this.amount.subtract(deductAmount);
    }

}













