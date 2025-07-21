package com.hhplus.ecommerce.order.domain;

import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    @Test
    void createOrderItem_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        OrderItem item = new OrderItem(p1, 2);

        assertEquals(BigDecimal.valueOf(1000), item.getUnitPrice());
        assertEquals(2, item.getQuantity());
    }

}








