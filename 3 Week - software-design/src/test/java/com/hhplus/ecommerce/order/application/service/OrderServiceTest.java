package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private BalanceUseCase balanceUseCase;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, productUseCase, balanceUseCase);
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

        Order result = orderService.createOrder(1L, List.of(1L), List.of(2), List.of());

        assertEquals(BigDecimal.valueOf(2000), result.getTotalPrice());
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(2, p1.getReservedStock());
    }

    @Test
    void createOrder_stock_insufficient() {
        Product p1 = new Product(1L, "P1", BigDecimal.valueOf(1000), 1, 0, 0L);
        when(productUseCase.getProduct(1L)).thenReturn(p1);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L, List.of(1L), List.of(2), List.of()));
    }

}












