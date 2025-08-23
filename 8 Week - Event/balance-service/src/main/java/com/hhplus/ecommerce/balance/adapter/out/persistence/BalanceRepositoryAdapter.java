package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BalanceRepositoryAdapter implements BalanceRepository {

    private final com.hhplus.ecommerce.balance.application.port.out.BalanceJpaRepository BalanceJpaRepository;

    @Override
    public Optional<Balance> findByUserId(long userId) {
        return BalanceJpaRepository.findById(userId)
                .map(BalanceJpaEntity::toDomain);
    }

    @Override
    public Balance save(Balance balance) {
        BalanceJpaEntity entity = BalanceJpaEntity.from(balance);
        BalanceJpaEntity saved = BalanceJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Balance> findByUserIdWithLock(long userId) {
        return BalanceJpaRepository.findByUserIdWithLock(userId)
                .map(BalanceJpaEntity::toDomain);
    }

}











