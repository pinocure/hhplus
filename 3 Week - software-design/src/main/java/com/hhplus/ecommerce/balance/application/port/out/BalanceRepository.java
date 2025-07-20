package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.domain.Balance;

import java.util.Optional;

/**
 * 역할: balance 리포지토리 인터페이스 (out port)
 * 책임: balance 데이터 접근을 추상화하여 도메인과 외부 저장소 간 의존성을 분리.
 */

public interface BalanceRepository {

    Optional<Balance> findByUserId(long userId);
    Balance save(Balance balance);

}
