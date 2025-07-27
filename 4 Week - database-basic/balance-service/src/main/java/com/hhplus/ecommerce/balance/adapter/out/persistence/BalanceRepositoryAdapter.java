package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 역할: balance 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: BalanceRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화
 */

@Repository
public class BalanceRepositoryAdapter implements BalanceRepository {

    // In-Memory 구현 -> JPA로 변경해야함
    private final Map<Long, Balance> store = new HashMap<>();


    @Override
    public Optional<Balance> findByUserId(long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Balance save(Balance balance) {
        store.put(balance.getUserId(), balance);
        return balance;
    }

}











