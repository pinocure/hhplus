package com.hhplus.ecommerce.balance.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceTest {

    @Test
    void charge_success() {
        Balance balance = new Balance(1L, BigDecimal.ZERO);
        balance.charge(new BigDecimal("1000"));
        assertEquals(new BigDecimal("1000"), balance.getAmount());
    }

    @Test
    void charge_invalid_amount() {
        Balance balance = new Balance(1L, BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> balance.charge(BigDecimal.ZERO));
    }

    @Test
    void deduct_success() {
        Balance balance = new Balance(1L, new BigDecimal("1000"));
        balance.deduct(new BigDecimal("500"));
        assertEquals(new BigDecimal("500"), balance.getAmount());
    }

    @Test
    void deduct_insufficient_amount() {
        Balance balance = new Balance(1L, new BigDecimal("100"));
        assertThrows(IllegalArgumentException.class, () -> balance.deduct(new BigDecimal("1000")));
    }

}










