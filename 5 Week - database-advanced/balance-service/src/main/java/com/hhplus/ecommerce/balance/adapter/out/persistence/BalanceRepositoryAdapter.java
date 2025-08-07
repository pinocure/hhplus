package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 역할: balance 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: BalanceRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화 및 도메인-JPA 엔티티 간 변환
 */

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

}











