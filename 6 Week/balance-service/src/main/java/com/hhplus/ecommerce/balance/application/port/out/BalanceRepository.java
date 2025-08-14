package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.domain.Balance;

import java.util.Optional;

public interface BalanceRepository {

    Optional<Balance> findByUserId(long userId);
    Balance save(Balance balance);
    Optional<Balance> findByUserIdWithLock(long userId);

}
