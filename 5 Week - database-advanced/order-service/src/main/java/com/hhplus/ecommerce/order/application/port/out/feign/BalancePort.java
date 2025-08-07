package com.hhplus.ecommerce.order.application.port.out.feign;

import java.math.BigDecimal;

/**
 * 역할: Balance 서비스와의 통신을 위한 출력 포트 인터페이스
 * 책임: 잔액 조회 및 차감 기능을 추상화하여 도메인이 외부 서비스 구현에 의존하지 않도록 함
 */

public interface BalancePort {

    BigDecimal getBalance(Long userId);
    void deductBalance(Long userId, BigDecimal amount);
    void refundBalance(Long userId, BigDecimal amount);

}
