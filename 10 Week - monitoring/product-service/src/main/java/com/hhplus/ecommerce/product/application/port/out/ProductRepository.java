package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();
    Optional<Product> findById(Long productId);
    List<Product> findPopular(int days, int limit);
    Product save(Product product);

    boolean checkProductVersion(Long productId, Long version);

    Optional<Product> findByIdWithLock(Long productId);

    void updateSalesStatistics(LocalDate salesDate, BigDecimal totalAmount);
}