package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.domain.Balance;

import java.util.Optional;

/**
 * 역할: balance 로드 관련 출력 포트 인터페이스
 * 책임: 잔액 데이터 로드 메서드를 정의하여 외부 어댑터가 이를 구현하도록 추상화
 */

public interface LoadBalancePort {

    Optional<Balance> loadBalance(Long userId);

}
