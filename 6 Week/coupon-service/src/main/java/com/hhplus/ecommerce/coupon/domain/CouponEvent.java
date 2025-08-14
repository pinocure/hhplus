package com.hhplus.ecommerce.coupon.domain;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
            throw new BusinessException(ErrorCode.COUPON_SOLD_OUT);
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
        this.remainingQuantity--;
    }

}













