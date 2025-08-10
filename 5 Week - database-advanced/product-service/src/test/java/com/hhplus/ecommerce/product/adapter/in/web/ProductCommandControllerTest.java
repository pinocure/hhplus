package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductCommandUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductCommandController.class)
public class ProductCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCommandUseCase productCommandUseCase;

    @Test
    void stock_deduct_success() throws Exception {
        doNothing().when(productCommandUseCase).deductStock(1L, 5);

        mockMvc.perform(post("/products/1/deduct")
                        .param("quantity", "5"))
                .andExpect(status().isOk());
    }

}









