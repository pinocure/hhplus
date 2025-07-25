package com.hhplus.ecommerce.order.domain;

import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void createOrder_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        OrderItem item = new OrderItem(p1, 2);
        Order order = new Order(1L, List.of(item), List.of());

        assertEquals(BigDecimal.valueOf(2000), order.getTotalPrice());
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    void confirm_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        OrderItem item = new OrderItem(p1, 2);
        Order order = new Order(1L, List.of(item), List.of());

        order.confirm();
        assertEquals("CONFIRMED", order.getStatus());
        assertEquals(2, p1.getReservedStock());
    }

    @Test
    void pay_success() {
        Order order = new Order(1L, List.of(), List.of());
        order.setStatus("CONFIRMED");
        order.pay();
        assertEquals("PAID", order.getStatus());
    }

    @Test
    void fail_and_rollback() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        p1.setReservedStock(2);
        OrderItem item = new OrderItem(p1, 2);
        OrderCoupon coupon = new OrderCoupon("CODE", BigDecimal.valueOf(500));
        coupon.setUsed(true);
        Order order = new Order(1L, List.of(item), List.of(coupon));

        order.fail();
        assertEquals("FAILED", order.getStatus());
        assertEquals(0, p1.getReservedStock());
        assertFalse(coupon.isUsed());
    }

}










