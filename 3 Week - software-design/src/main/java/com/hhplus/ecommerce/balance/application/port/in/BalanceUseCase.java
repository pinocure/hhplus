package com.hhplus.ecommerce.balance.application.port.in;

import java.math.BigDecimal;

/**
 * 역할: balance 도메인의 입력 포트 인터페이스.
 * 책임: 잔액 관련 유즈케이스(충전, 조회)를 정의하여 애플리케이션 서비스가 이를 구현하도록 함
 */

public interface BalanceUseCase {

    BigDecimal chargeBalance(Long userId, BigDecimal amount);   // 잔액 충전
    BigDecimal getBalance(Long userId);                         // 잔액 조회

}
