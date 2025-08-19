package com.hhplus.ecommerce.product.service;

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
import static org.mockito.ArgumentMatchers.any;
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

        assertThrows(Exception.class, () -> productService.getAllProducts());
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

        assertThrows(Exception.class, () -> productService.getProduct(1L));
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
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        assertDoesNotThrow(() -> productService.reserveStock(1L, 5, 0L));

        assertEquals(5, p1.getReservedStock());
        assertEquals(1L, p1.getVersion());
    }

    @Test
    void reserveStock_concurrencyConflict() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(p1));
        when(productRepository.checkProductVersion(1L, 0L)).thenReturn(false);

        assertDoesNotThrow(() -> productService.reserveStock(1L, 5, 0L));
        assertEquals(5, p1.getReservedStock());
    }

    @Test
    void deductStock_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        p1.setReservedStock(5);
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        assertDoesNotThrow(() -> productService.deductStock(1L, 5));

        assertEquals(5, p1.getStock());
        assertEquals(0, p1.getReservedStock());
    }

    @Test
    void rollbackReservedStock_success() {
        Product p1 = new Product(1L, "P1", BigDecimal.TEN, 10, 0, 0L);
        p1.setReservedStock(5);
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(p1));
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        assertDoesNotThrow(() -> productService.rollBackStock(1L, 5));

        assertEquals(0, p1.getReservedStock());
    }

}












