package com.hhplus.ecommerce.coupon.domain;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
        this.used = true;
    }

}













