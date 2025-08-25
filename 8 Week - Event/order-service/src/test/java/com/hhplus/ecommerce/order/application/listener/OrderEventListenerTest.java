package com.hhplus.ecommerce.order.application.listener;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class OrderEventListenerTest {

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    @DisplayName("주문 완료 이벤트 처리 성공")
    void handleOrderCompleted_success() throws Exception {
        OrderCompletedEvent event = new OrderCompletedEvent(
                1L,
                100L,
                new BigDecimal("50000"),
                LocalDateTime.now()
        );

        assertDoesNotThrow(() -> {
            orderEventListener.handleOrderCompleted(event);
        });
    }

}









