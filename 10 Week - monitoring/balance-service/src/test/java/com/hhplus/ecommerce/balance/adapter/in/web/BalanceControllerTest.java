package com.hhplus.ecommerce.balance.adapter.in.web;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BalanceUseCase balanceUseCase;

    @InjectMocks
    private BalanceController balanceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }

    @Test
    void charge_success() throws Exception {
        when(balanceUseCase.chargeBalance(1L, new BigDecimal("1000"))).thenReturn(new BigDecimal("1000"));

        mockMvc.perform(post("/balances/charge")
                        .param("userId", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000));
    }

    @Test
    void getBalance_success() throws Exception {
        when(balanceUseCase.getBalance(1L)).thenReturn(new BigDecimal("1000"));

        mockMvc.perform(get("/balances/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000));
    }

}










