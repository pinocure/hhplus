package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(orderRepository, kafkaTemplate);

        ReflectionTestUtils.setField(orderService, "processingStatus", new ConcurrentHashMap<>());
    }

    @Test
    @DisplayName("주문 성공")
    void createOrder_success() {
        OrderProduct orderProduct = new OrderProduct(1L, "Test Product", BigDecimal.valueOf(1000));
        orderProduct.setId(1L);

        when(orderRepository.saveOrderProduct(any(OrderProduct.class))).thenReturn(orderProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        Order result = orderService.createOrder(1L, List.of(1L), List.of(2), List.of());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(1L, result.getUserId());
        assertNotNull(result.getItems());
        assertFalse(result.getItems().isEmpty());

        verify(orderRepository).saveOrderProduct(any(OrderProduct.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 실패 - 상품 ID와 수량 개수 불일치")
    void createOrder_stock_insufficient() {
        when(orderRepository.saveOrderProduct(any(OrderProduct.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            orderService.createOrder(1L, List.of(1L, 2L), List.of(2), List.of());
        });
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
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.payOrder(1L);

        assertEquals("PROCESSING", result.getStatus());
        verify(kafkaTemplate, times(2)).send(anyString(), any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("결제 요청 실패 - 잘못된 주문 상태")
    void payOrder_invalidStatus() {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.payOrder(1L));
        assertEquals(ErrorCode.ORDER_FAIL, exception.getErrorCode());
    }

}












