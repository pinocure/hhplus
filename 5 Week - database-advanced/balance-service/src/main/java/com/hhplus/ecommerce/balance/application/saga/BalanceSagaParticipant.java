package com.hhplus.ecommerce.balance.application.saga;

import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import com.hhplus.ecommerce.common.event.CompensationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 역할: Balance Saga 참여자
 * 책임: 분산 트랜잭션에서 Balance 서비스의 역할을 수행하고 보상 트랜잭션을 처리
 */

@Component
public class BalanceSagaParticipant {

    private final BalanceRepository balanceRepository;

    public BalanceSagaParticipant(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @EventListener
    @Transactional
    public void handleCompensation(CompensationEvent event) {
        if ("BALANCE_REFUND".equals(event.getCompensationType())) {
            refundBalance(event);
        }
    }

    private void refundBalance(CompensationEvent event) {
        // 보상 데이터에서 userId, amount 추출
        @SuppressWarnings("unchecked")
        var compensationData = (java.util.Map<String, Object>) event.getCompensationData();

        Long userId = ((Number) compensationData.get("userId")).longValue();
        BigDecimal amount = new BigDecimal(compensationData.get("amount").toString());

        Balance balance = balanceRepository.findByUserId(userId)
                .orElse(new Balance(userId, BigDecimal.ZERO));

        balance.charge(amount);
        balanceRepository.save(balance);
    }

}







