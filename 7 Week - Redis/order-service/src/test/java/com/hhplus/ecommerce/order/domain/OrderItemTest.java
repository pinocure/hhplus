package com.hhplus.ecommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderItemTest {

    @Test
    void createOrderItem_success() {
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.valueOf(1000), item.getUnitPrice());
        assertEquals(2, item.getQuantity());
    }

}








