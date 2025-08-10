package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.BalanceFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


/**
 * 역할: Balance 서비스와의 통신을 담당하는 Feign 기반 어댑터
 * 책임: Feign Client를 통해 Balance 서비스의 기능을 호출
 */

@Component
@RequiredArgsConstructor
public class BalanceFeignAdapter implements BalancePort {

    private final BalanceFeignClient balanceFeignClient;

    @Override
    public BigDecimal getBalance(Long userId) {
        try {
            return balanceFeignClient.getBalance(userId);
        } catch (FeignException e) {
            throw new RuntimeException("잔액 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void deductBalance(Long userId, BigDecimal amount) {
        try {
            balanceFeignClient.deductBalance(userId, amount);
        } catch (FeignException e) {
            throw new RuntimeException("잔액 차감 실패 : " + e.getMessage());
        }
    }
}









