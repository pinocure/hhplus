package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.BalanceFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 역할: BalancePort의 구현체로서 Feign Client를 사용한 Balance 서비스 통신 어댑터
 * 책임: Feign Client 호출, 예외 처리, 에러 메시지 변환 등 기술적 세부사항을 처리
 */

@Component
public class BalanceFeignAdapter implements BalancePort {

    private final BalanceFeignClient balanceFeignClient;

    public BalanceFeignAdapter(BalanceFeignClient balanceFeignClient) {
        this.balanceFeignClient = balanceFeignClient;
    }


    @Override
    public BigDecimal getBalance(Long userId) {
        try {
            return balanceFeignClient.getBalance(userId);
        } catch (Exception e) {
            throw new RuntimeException("잔액 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void deductBalance(Long userId, BigDecimal amount) {
        try {
            balanceFeignClient.deductBalance(userId, amount);
        } catch (Exception e) {
            throw new RuntimeException("잔액 차감 실패 : " + e.getMessage());
        }
    }

    @Override
    public void refundBalance(Long userId, BigDecimal amount) {
        try {
            balanceFeignClient.refundBalance(userId, amount);
        } catch (Exception e) {
            throw new RuntimeException("잔액 환불 실패 : " + e.getMessage());
        }
    }

}









