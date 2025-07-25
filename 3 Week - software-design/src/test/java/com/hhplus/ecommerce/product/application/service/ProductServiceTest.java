package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.application.service.ProductService;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findAll()).thenReturn(List.of(p1));

        List<Product> result = productService.getAllProducts();
        assertEquals(1, result.size());
    }

    @Test
    void getAllProducts_empty() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> productService.getAllProducts());
    }

    @Test
    void getProduct_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));

        Product result = productService.getProduct(1L);
        assertEquals("P1", result.getName());
    }

    @Test
    void getProduct_not_found() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getProduct(1L));
    }

    @Test
    void getPopularProducts_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findPopular(3, 5)).thenReturn(List.of(p1));

        List<Product> result = productService.getPopularProducts(3, 5);
        assertEquals(1, result.size());
    }

}












