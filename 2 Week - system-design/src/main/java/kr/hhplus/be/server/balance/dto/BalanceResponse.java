package kr.hhplus.be.server.balance.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long userId,
        BigDecimal balance
) {}
