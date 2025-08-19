package com.hhplus.ecommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        // MSA 환경에서는 실제 재고 변경은 외부 서비스에서 처리되므로
        // 여기서는 상태 변경만 검증
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
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));
        OrderCoupon coupon = new OrderCoupon("CODE", BigDecimal.valueOf(500));
        coupon.setUsed(true);
        Order order = new Order(1L, List.of(item), List.of(coupon));

        order.fail();
        assertEquals("FAILED", order.getStatus());
        assertFalse(coupon.isUsed());
        // MSA 환경에서는 실제 재고 롤백은 외부 서비스에서 처리되므로
        // 여기서는 상태 변경과 쿠폰 사용 취소만 검증
    }

}










