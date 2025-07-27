package com.hhplus.ecommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderItemTest {

    @Test
    void createOrderItem_success() {
        OrderProduct orderProduct = new OrderProduct(1L, "P1", BigDecimal.valueOf(1000));
        OrderItem item = new OrderItem(orderProduct, 2);

        assertEquals(BigDecimal.valueOf(1000), item.getUnitPrice());
        assertEquals(2, item.getQuantity());
    }

}








