package com.hhplus.ecommerce.balance.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.LockTimeoutException;

import java.math.BigDecimal;
import java.util.Optional;

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
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        }
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(Balance::getAmount)
                .orElseThrow(() -> new BusinessException(ErrorCode.BALANCE_NOT_FOUND));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deductBalance(Long userId, BigDecimal amount) {
        try {
            Optional<Balance> optionalBalance = balanceRepository.findByUserIdWithLock(userId);
            Balance balance = optionalBalance.orElseThrow(() -> new BusinessException(ErrorCode.BALANCE_NOT_FOUND));
            balance.deduct(amount);
            balanceRepository.save(balance);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        }
    }

}











