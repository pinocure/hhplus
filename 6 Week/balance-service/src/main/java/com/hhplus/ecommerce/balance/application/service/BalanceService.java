package com.hhplus.ecommerce.balance.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.LockTimeoutException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 역할: balance 서비스 구현 클래스
 * 책임: BalanceUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용
 */

@Service
public class BalanceService implements BalanceUseCase {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal chargeBalance(Long userId, BigDecimal amount) {
        try {
            Optional<Balance> optionalBalance = balanceRepository.findByUserIdWithLock(userId);
            Balance balance = optionalBalance.orElse(new Balance(userId, BigDecimal.ZERO));
            balance.charge(amount);
            Balance saved = balanceRepository.save(balance);
            return saved.getAmount();
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalStateException("잔액 처리 중 잠금 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(Balance::getAmount)
                .orElseThrow(() -> new RuntimeException("사용자 잔액이 없습니다."));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deductBalance(Long userId, BigDecimal amount) {
        try {
            Optional<Balance> optionalBalance = balanceRepository.findByUserIdWithLock(userId);
            Balance balance = optionalBalance.orElseThrow(() -> new IllegalArgumentException("사용자의 잔액 정보가 없습니다."));
            balance.deduct(amount);
            balanceRepository.save(balance);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalStateException("잔액 처리 중 잠금 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

}











