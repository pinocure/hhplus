package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.BalanceFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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









