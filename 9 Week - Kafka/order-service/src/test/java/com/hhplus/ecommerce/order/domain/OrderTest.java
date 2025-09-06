package com.hhplus.ecommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    @Test
    void createOrder_success() {
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));
        Order order = new Order(1L, List.of(item), List.of());

        assertEquals(BigDecimal.valueOf(2000), order.getTotalPrice());
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    void confirm_success() {
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));
        Order order = new Order(1L, List.of(item), List.of());

        order.confirm();

        assertEquals("CONFIRMED", order.getStatus());
    }

    @Test
    void pay_success() {
        Order order = new Order(1L, List.of(), List.of());
        order.setStatus("CONFIRMED");

        order.pay();

        assertEquals("PAID", order.getStatus());
    }

}










