package com.hhplus.ecommerce.product.adapter.out.persistence;

import com.hhplus.ecommerce.product.application.port.out.ProductJpaRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(ProductJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findPopular(int days, int limit) {
        return productJpaRepository.findAll(PageRequest.of(0, limit)).stream()
                .map(ProductJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public boolean checkProductVersion(Long productId, Long version) {
        return productJpaRepository.findById(productId)
                .map(entity -> entity.getVersion().equals(version))
                .orElse(false);
    }

    @Override
    public Optional<Product> findByIdWithLock(Long productId) {
        return productJpaRepository.findByIdWithLock(productId)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public void updateSalesStatistics(LocalDate salesDate, BigDecimal totalAmount) {
        System.out.println("판매 통계 업데이트: date=" + salesDate +
                ", amount=" + totalAmount);
    }

}










