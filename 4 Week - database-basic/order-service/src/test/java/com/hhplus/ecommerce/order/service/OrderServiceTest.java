package com.hhplus.ecommerce.order.service;

import com.hhplus.ecommerce.order.adapter.out.external.ProductClientAdapter;
import com.hhplus.ecommerce.order.application.port.out.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.ProductPort;
import com.hhplus.ecommerce.order.application.service.OrderService;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
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
    private ProductPort productPort;

    @Mock
    private CouponPort couponPort;

    @Mock
    private BalancePort balancePort;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, productPort, couponPort, balancePort);
    }

    @Test
    void createOrder_success() {
        ProductPort.ProductDto productDto = new ProductPort.ProductDto();
        productDto.setId(1L);
        productDto.setName("P1");
        productDto.setPrice(BigDecimal.valueOf(1000));
        productDto.setStock(10);

        OrderProduct orderProduct = new OrderProduct(1L, "P1", BigDecimal.valueOf(1000));
        orderProduct.setId(1L);

        // Port Mock 설정
        when(productPort.getProduct(1L)).thenReturn(productDto);
        when(orderRepository.saveOrderProduct(any(OrderProduct.class))).thenReturn(orderProduct);
        doNothing().when(couponPort).validateCoupon("CODE");
        when(couponPort.getCouponDiscountAmount("CODE")).thenReturn(BigDecimal.valueOf(500)); // 쿠폰 할인 추가
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        Order result = orderService.createOrder(1L, List.of(1L), List.of(2), List.of("CODE"));

        // 총액 = (상품가격 * 수량) - 쿠폰할인 = (1000 * 2) - 500 = 1500
        assertEquals(BigDecimal.valueOf(1500), result.getTotalPrice());
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void createOrder_stock_insufficient() {
        ProductClientAdapter.ProductDto productDto = new ProductClientAdapter.ProductDto();
        productDto.setId(1L);
        productDto.setName("P1");
        productDto.setPrice(BigDecimal.valueOf(1000));
        productDto.setStock(1);

        when(productPort.getProduct(1L)).thenReturn(productDto);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L, List.of(1L), List.of(2), List.of("CODE")));
    }

    @Test
    void payOrder_success() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("CONFIRMED");
        order.setTotalPrice(BigDecimal.valueOf(1000));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(balancePort).deductBalance(anyLong(), any(BigDecimal.class));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.payOrder(1L);

        assertEquals("PAID", result.getStatus());
        verify(balancePort).deductBalance(1L, BigDecimal.valueOf(1000));
    }

    @Test
    void payOrder_failure_rollback() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("CONFIRMED");
        order.setTotalPrice(BigDecimal.valueOf(1000));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new RuntimeException("잔액 부족")).when(balancePort).deductBalance(anyLong(), any(BigDecimal.class));
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












