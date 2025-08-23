package com.hhplus.ecommerce.order.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.application.service.OrderService;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("주문 성공")
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
    @DisplayName("주문 실패 - 재고 부족")
    void createOrder_stock_insufficient() {
        ProductPort.ProductDto productDto = new ProductPort.ProductDto();
        productDto.setId(1L);
        productDto.setName("P1");
        productDto.setPrice(BigDecimal.valueOf(1000));
        productDto.setStock(1);

        when(productPort.getProduct(1L)).thenReturn(productDto);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L, List.of(1L), List.of(2), List.of("CODE")));
    }

    @Test
    @DisplayName("결제 성공")
    void payOrder_success() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("CONFIRMED");
        order.setTotalPrice(BigDecimal.valueOf(1000));

        // 비관적 락 사용
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(balancePort).deductBalance(anyLong(), any(BigDecimal.class));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.payOrder(1L);

        assertEquals("PAID", result.getStatus());
        verify(balancePort).deductBalance(1L, BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("결제 실패 - 되돌리기")
    void payOrder_failure_rollback() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("CONFIRMED");
        order.setTotalPrice(BigDecimal.valueOf(1000));

        // 비관적 락 사용
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new RuntimeException("잔액 부족")).when(balancePort).deductBalance(anyLong(), any(BigDecimal.class));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(BusinessException.class, () -> orderService.payOrder(1L));
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    @DisplayName("결제 실패 - 마감")
    void payOrder_invalidStatus() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("PENDING");

        // 비관적 락 사용
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.payOrder(1L));

        assertEquals(ErrorCode.ORDER_FAIL, exception.getErrorCode());
        assertEquals("PENDING", order.getStatus());
    }

}












