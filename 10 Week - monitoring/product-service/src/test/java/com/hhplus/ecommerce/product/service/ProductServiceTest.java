package com.hhplus.ecommerce.product.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.application.service.ProductService;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(productRepository);
        ReflectionTestUtils.setField(productService, "redisTemplate", null);
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

        assertThrows(BusinessException.class, () -> productService.getAllProducts());
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

        assertThrows(BusinessException.class, () -> productService.getProduct(1L));
    }

    @Test
    void getPopularProducts_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findPopular(3, 5)).thenReturn(List.of(p1));

        List<Product> result = productService.getPopularProducts(3, 5);
        assertEquals(1, result.size());
    }

    @Test
    void reserveStock_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        assertDoesNotThrow(() -> productService.reserveStock(1L, 5, 0L));

        assertEquals(5, p1.getReservedStock());
        assertEquals(1L, p1.getVersion());
    }

    @Test
    void deductStock_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        assertDoesNotThrow(() -> productService.deductStock(1L, 5));

        assertEquals(5, p1.getStock());
    }

}












