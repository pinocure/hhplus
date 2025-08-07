package com.hhplus.ecommerce.balance.application.service;

import com.hhplus.ecommerce.balance.adapter.out.persistence.lock.BalanceLockAdapter;
import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 역할: balance 서비스 구현 클래스
 * 책임: BalanceUseCase를 구현하며, 비관적 락을 통한 동시성 제어와 함께 유즈케이스 흐름을 조율
 */

@Service
public class BalanceService implements BalanceUseCase {

    private final BalanceRepository balanceRepository;
    private final BalanceLockAdapter balanceLockAdapter;

    public BalanceService(BalanceRepository balanceRepository, BalanceLockAdapter balanceLockAdapter) {
        this.balanceRepository = balanceRepository;
        this.balanceLockAdapter = balanceLockAdapter;
    }

    @Override
    @Transactional
    public BigDecimal chargeBalance(Long userId, BigDecimal amount) {

        Optional<Balance> optionalBalance = balanceRepository.findByUserId(userId);
        Balance balance = optionalBalance.orElse(new Balance(userId, BigDecimal.ZERO));
        balance.charge(amount);
        Balance saved = balanceRepository.save(balance);

        return saved.getAmount();
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(Balance::getAmount)
                .orElseThrow(() -> new RuntimeException("사용자 잔액이 없습니다."));
    }

    @Override
    @Transactional
    public void deductBalance(Long userId, BigDecimal amount) {

        Optional<Balance> optionalBalance = balanceRepository.findByUserId(userId);
        Balance balance = optionalBalance.orElseThrow(() -> new IllegalArgumentException("사용자의 잔액 정보가 없습니다."));
        balance.deduct(amount);
        balanceRepository.save(balance);
    }

    @Transactional
    public void refundBalance(Long userId, BigDecimal amount) {

        Optional<Balance> optionalBalance = balanceRepository.findByUserId(userId);
        Balance balance = optionalBalance.orElse(new Balance(userId, BigDecimal.ZERO));
        balance.charge(amount);
        balanceRepository.save(balance);
    }

}











