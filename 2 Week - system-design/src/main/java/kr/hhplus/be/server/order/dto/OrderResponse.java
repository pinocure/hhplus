package kr.hhplus.be.server.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long userId,
        Long productId,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt
) { }
