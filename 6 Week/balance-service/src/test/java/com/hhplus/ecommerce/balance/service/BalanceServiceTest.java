package com.hhplus.ecommerce.balance.service;

import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.application.service.BalanceService;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        balanceService = new BalanceService(balanceRepository);
    }

    @Test
    void chargeBalance_new_user() {
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal result = balanceService.chargeBalance(1L, new BigDecimal("1000"));

        assertEquals(new BigDecimal("1000"), result);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void chargeBalance_existing_user() {
        Balance existing = new Balance(1L, new BigDecimal("500"));
        when(balanceRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(existing));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal result = balanceService.chargeBalance(1L, new BigDecimal("500"));

        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void getBalance_success() {
        Balance balance = new Balance(1L, new BigDecimal("1000"));
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.of(balance));

        BigDecimal result = balanceService.getBalance(1L);

        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void getBalance_not_found() {
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> balanceService.getBalance(1L));
    }

    @Test
    void deductBalance_success() {
        Balance balance = new Balance(1L, new BigDecimal("1000"));
        when(balanceRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        assertDoesNotThrow(() -> balanceService.deductBalance(1L, new BigDecimal("500")));
        assertEquals(new BigDecimal("500"), balance.getAmount());
    }

    @Test
    void deductBalance_insufficient() {
        Balance balance = new Balance(1L, new BigDecimal("100"));
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.of(balance));

        assertThrows(Exception.class, () -> balanceService.deductBalance(1L, new BigDecimal("200")));
    }

}












