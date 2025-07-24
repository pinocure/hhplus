package com.hhplus.ecommerce.product.adapter.out.persistence;

import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 역할: product 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: ProductRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화
 */

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    // In-Memory 구현 -> JPA로 변경해야함
    private final Map<Long, Product> store = new HashMap<>();

    public ProductRepositoryAdapter() {
        store.put(1L, new Product(1L, "Product1", new BigDecimal("1000"), 10, 0, 0L));
        store.put(2L, new Product(2L, "Product2", new BigDecimal("2000"), 20, 0, 0L));
    }


    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(store.get(productId));
    }

    @Override
    public List<Product> findPopular(int days, int limit) {
        return store.values().stream()
                .sorted(Comparator.comparingInt(p -> p.getStock()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public boolean checkProductVersion(Long productId, Long version) {
        Product product = store.get(productId);
        if (product == null) {
            return false;
        }
        return product.getVersion().equals(version);
    }

}










