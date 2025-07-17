package kr.hhplus.be.server.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String name,
        String code,
        String type,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        int totalQuantity,
        int usedQuantity,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        boolean isActive
) {}
