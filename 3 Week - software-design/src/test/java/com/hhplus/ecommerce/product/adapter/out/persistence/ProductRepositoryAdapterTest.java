package com.hhplus.ecommerce.product.adapter.out.persistence;

import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryAdapterTest {

    private final ProductRepositoryAdapter repository = new ProductRepositoryAdapter();

    @Test
    void findAll_success() {
        List<Product> products = repository.findAll();
        assertFalse(products.isEmpty());
    }

    @Test
    void findById_success() {
        Optional<Product> found = repository.findById(1L);
        assertTrue(found.isPresent());
    }

    @Test
    void findById_not_found() {
        Optional<Product> found = repository.findById(3L);
        assertFalse(found.isPresent());
    }

    @Test
    void findPopular_success() {
        List<Product> popular = repository.findPopular(3, 5);
        assertFalse(popular.isEmpty());
    }

    @Test
    void save_success() {
        Product newProduct = new Product(3L, "New", BigDecimal.valueOf(3000), 30, 0, 0L);
        Product saved = repository.save(newProduct);
        assertEquals("New", saved.getName());
    }

    @Test
    void checkProductVersion_success() {
        Product product = new Product(3L, "New", BigDecimal.valueOf(3000), 30, 0, 0L);
        repository.save(product);
        boolean result = repository.checkProductVersion(3L, 0L);
        assertTrue(result);
    }

    @Test
    void checkProductVersion_conflict() {
        Product product = new Product(3L, "New", BigDecimal.valueOf(3000), 30, 0, 0L);
        product.setVersion(1L);
        repository.save(product);
        boolean result = repository.checkProductVersion(3L, 0L);
        assertFalse(result);
    }

}









