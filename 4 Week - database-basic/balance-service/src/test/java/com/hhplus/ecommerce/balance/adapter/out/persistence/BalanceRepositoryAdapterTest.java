package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.domain.Balance;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceRepositoryAdapterTest {

    private final BalanceRepositoryAdapter repository = new BalanceRepositoryAdapter();

    @Test
    void save_and_find() {
        Balance balance = new Balance(1L, new BigDecimal("1000"));
        repository.save(balance);

        Optional<Balance> found = repository.findByUserId(1L);
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("1000"), found.get().getAmount());
    }

    @Test
    void find_not_found() {
        Optional<Balance> found = repository.findByUserId(2L);
        assertFalse(found.isPresent());
    }

}









