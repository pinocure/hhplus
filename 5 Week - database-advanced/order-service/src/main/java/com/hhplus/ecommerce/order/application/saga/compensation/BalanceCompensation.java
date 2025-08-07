package com.hhplus.ecommerce.order.application.saga.compensation;

import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 역할: Balance 서비스 보상 트랜잭션 처리기
 * 책임: 주문 실패 시 차감된 잔액을 원복하는 보상 트랜잭션을 실행
 */

@Component
public class BalanceCompensation {

    private final BalancePort balancePort;

    public BalanceCompensation(BalancePort balancePort) {
        this.balancePort = balancePort;
    }

    public void compensate(Long userId, BigDecimal amount) {
        try {
            balancePort.refundBalance(userId, amount);
        } catch (Exception e) {
            throw new RuntimeException("Balance 보상 실패 : ", e);
        }
    }

}






