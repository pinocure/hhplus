package com.hhplus.ecommerce.balance.application.port.in;

import java.math.BigDecimal;

public interface BalanceUseCase {

    BigDecimal chargeBalance(Long userId, BigDecimal amount);   // 잔액 충전
    BigDecimal getBalance(Long userId);                         // 잔액 조회

    void deductBalance(Long userId, BigDecimal amount);
}
