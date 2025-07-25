package com.hhplus.ecommerce.product.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    void hasEnoughStock_success() {
        Product product = new Product(1L, "Test", BigDecimal.TEN, 10, 0, 0L);
        assertTrue(product.hasEnoughStock(5));
    }

    @Test
    void hasEnoughStock_fail() {
        Product product = new Product(1L, "Test", BigDecimal.TEN, 10, 0, 0L);
        assertFalse(product.hasEnoughStock(15));
    }

    @Test
    void reserveStock_success() {
        Product product = new Product(1L, "Test", BigDecimal.TEN, 10, 0, 0L);
        product.reserveStock(5);
        assertEquals(5, product.getReservedStock());
    }

    @Test
    void reserveStock_fail() {
        Product product = new Product(1L, "Test", BigDecimal.TEN, 10, 0, 0L);
        assertThrows(IllegalArgumentException.class, () -> product.reserveStock(15));
    }

}










