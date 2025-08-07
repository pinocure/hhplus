package com.hhplus.ecommerce.order.application.saga;

import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class OrderSagaOrchestratorTest {

    @Mock
    private BalancePort balancePort;
    @Mock
    private ProductPort productPort;
    @Mock
    private CouponPort couponPort;
    @Mock
    private OrderSagaState orderSagaState;

    private OrderSagaOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orchestrator = new OrderSagaOrchestrator(balancePort, productPort, couponPort, orderSagaState);
    }

    @Test
    void startOrderSaga_success() {
        // given
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));
        Order order = new Order(1L, List.of(item), List.of());
        order.setId(1L);

        doNothing().when(orderSagaState).create(anyString(), anyLong());
        doNothing().when(productPort).reserveStock(anyLong(), anyInt());
        doNothing().when(orderSagaState).updateStep(anyString(), anyString());
        doNothing().when(orderSagaState).complete(anyString());

        // when & then
        assertDoesNotThrow(() -> orchestrator.startOrderSaga(order));
        verify(productPort).reserveStock(1L, 2);
        verify(orderSagaState).complete(anyString());
    }

    @Test
    void startOrderSaga_failureWithCompensation() {
        // given
        OrderItem item = new OrderItem(1L, 2, BigDecimal.valueOf(1000));
        OrderCoupon coupon = new OrderCoupon("COUPON123", BigDecimal.valueOf(500));
        Order order = new Order(1L, List.of(item), List.of(coupon));
        order.setId(1L);

        doNothing().when(orderSagaState).create(anyString(), anyLong());
        doNothing().when(productPort).reserveStock(anyLong(), anyInt());
        doNothing().when(orderSagaState).updateStep(anyString(), eq("STOCK_RESERVED"));
        doThrow(new RuntimeException("쿠폰 검증 실패")).when(couponPort).validateCoupon(anyString());
        when(orderSagaState.getLastStep(anyString())).thenReturn("STOCK_RESERVED");
        doNothing().when(productPort).cancelReservation(anyLong(), anyInt());
        doNothing().when(orderSagaState).fail(anyString());

        // when & then
        assertThrows(RuntimeException.class, () -> orchestrator.startOrderSaga(order));
        verify(productPort).cancelReservation(1L, 2);
        verify(orderSagaState).fail(anyString());
    }

}







