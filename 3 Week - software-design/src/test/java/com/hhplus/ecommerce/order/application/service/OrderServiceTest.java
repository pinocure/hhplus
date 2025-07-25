package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private CouponUseCase couponUseCase;

    @Mock
    private BalanceUseCase balanceUseCase;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, productUseCase, couponUseCase, balanceUseCase);
    }

    @Test
    void createOrder_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        when(productUseCase.getProduct(1L)).thenReturn(p1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);

            return order;
        });

        Order result = orderService.createOrder(1L, List.of(1L), List.of(2), List.of("CODE"));

        assertEquals(BigDecimal.valueOf(2000), result.getTotalPrice());
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, p1.getReservedStock());
    }

    @Test
    void createOrder_stock_insufficient() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 1, 0, 0L);
        when(productUseCase.getProduct(1L)).thenReturn(p1);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L, List.of(1L), List.of(2), List.of("CODE")));
    }

    @Test
    void payOrder_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 10, 0, 0L);
        p1.setReservedStock(2);
        OrderItem item = new OrderItem(p1, 2);
        OrderCoupon coupon = new OrderCoupon("CODE", BigDecimal.valueOf(500));
        Order order = new Order(1L, List.of(item), List.of(coupon));
        order.setId(1L);
        order.setStatus("CONFIRMED");
        order.setTotalPrice(BigDecimal.valueOf(1500));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(balanceUseCase).deductBalance(anyLong(), any(BigDecimal.class));
        doAnswer(invocation -> {
            Long orderId = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            p1.deductStock(quantity);
            return null;
        }).when(productUseCase).deductStock(anyLong(), anyInt());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.payOrder(1L);

        assertEquals("PAID", result.getStatus());
        assertEquals(8, p1.getStock());
        assertEquals(0, p1.getReservedStock());
        assertTrue(coupon.isUsed());
    }

    @Test
    void payOrder_failure_rollback() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("CONFIRMED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new RuntimeException("잔액 부족")).when(balanceUseCase).deductBalance(anyLong(), any(BigDecimal.class));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(RuntimeException.class, () -> orderService.payOrder(1L));
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void payOrder_invalidStatus() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.payOrder(1L));
    }

}












