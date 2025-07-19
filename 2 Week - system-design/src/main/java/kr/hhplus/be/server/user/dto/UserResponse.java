package kr.hhplus.be.server.user.dto;

import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String name,
        String email,
        BigDecimal balance
) {}
