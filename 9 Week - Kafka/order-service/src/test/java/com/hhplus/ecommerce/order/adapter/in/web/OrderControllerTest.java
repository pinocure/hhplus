package com.hhplus.ecommerce.order.adapter.in.web;

import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderUseCase orderUseCase;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("주문 성공")
    void createOrder_success() throws Exception {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setTotalPrice(BigDecimal.valueOf(2000));
        when(orderUseCase.createOrder(1L, List.of(1L), List.of(2), List.of())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .param("userId", "1")
                        .param("productIds", "1")
                        .param("quantities", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(2000));
    }

    @Test
    @DisplayName("결제 성공")
    void payOrder_success() throws Exception {
        Order order = new Order(1L, List.of(), List.of());
        order.setId(1L);
        order.setStatus("PAID");
        when(orderUseCase.payOrder(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/1/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

}










