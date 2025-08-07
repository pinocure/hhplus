package com.hhplus.ecommerce.balance.adapter.out.persistence.lock;

import com.hhplus.ecommerce.balance.adapter.out.persistence.BalanceJpaEntity;
import com.hhplus.ecommerce.balance.application.port.out.BalanceJpaRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 역할: Balance 비관적 락 어댑터
 * 책임: 잔액 조회 및 업데이트 시 비관적 락을 적용하여 동시성 문제를 해결
 */

@Component
public class BalanceLockAdapter {

    private final BalanceJpaRepository balanceJpaRepository;

    public BalanceLockAdapter(final BalanceJpaRepository balanceJpaRepository) {
        this.balanceJpaRepository = balanceJpaRepository;
    }

    @Transactional
    public Optional<Balance> findByUserIdWithLock(Long userId) {
        return balanceJpaRepository.findByIdWithLock(userId)
                .map(BalanceJpaEntity::toDomain);
    }

}













