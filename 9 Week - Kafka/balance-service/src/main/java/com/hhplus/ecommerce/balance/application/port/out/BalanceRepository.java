package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.domain.Balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BalanceRepository {

    Optional<Balance> findByUserId(long userId);
    Balance save(Balance balance);
    Optional<Balance> findByUserIdWithLock(long userId);

    void recordBalanceUsage(Long userId, Long orderId, BigDecimal amount, BigDecimal remainingBalance, LocalDateTime usedAt);

}
