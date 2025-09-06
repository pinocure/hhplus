package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void getAll_success() throws Exception {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productUseCase.getAllProducts()).thenReturn(List.of(p1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("P1"));
    }

    @Test
    void getProduct_success() throws Exception {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productUseCase.getProduct(1L)).thenReturn(p1);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("P1"));
    }

    @Test
    void getPopular_success() throws Exception {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productUseCase.getPopularProducts(3, 5)).thenReturn(List.of(p1));

        mockMvc.perform(get("/products/popular").param("days", "3").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("P1"));
    }

}










