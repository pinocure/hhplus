package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductQueryUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductQueryController.class)
public class ProductQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductQueryUseCase productQueryUseCase;

    @Test
    void oneProduct_search_success() throws Exception {
        Product product = new Product(1L, "테스트 상품", new BigDecimal("10000"), 10, 0, 0L);
        when(productQueryUseCase.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("테스트 상품"));
    }

}









