package com.hhplus.ecommerce.product.adapter.out.persistence.query;

import com.hhplus.ecommerce.product.adapter.out.persistence.ProductJpaEntity;
import com.hhplus.ecommerce.product.application.port.out.PopularProductRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductJpaRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductQueryRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 역할: Product Query 어댑터
 * 책임: 읽기 최적화된 쿼리를 통해 상품 조회 기능을 제공 (CQRS Read Model)
 */

@Component
public class ProductQueryAdapter implements ProductQueryRepository {

    private final ProductJpaRepository productJpaRepository;
    private final PopularProductRepository popularProductRepository;

    public ProductQueryAdapter(ProductJpaRepository productJpaRepository, PopularProductRepository popularProductRepository) {
        this.productJpaRepository = productJpaRepository;
        this.popularProductRepository = popularProductRepository;
    }

    @Cacheable(value = "products", key = "#productId")
    public Optional<Product> findProductById(Long productId) {
        return productJpaRepository.findById(productId)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(ProductJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "popularProducts", key = "#days + '-' + #limit")
    public List<Product> findPopularProducts(int days, int limit) {
        // 여기 다시보기
        return productJpaRepository.findAll(PageRequest.of(0, limit)).stream()
                .map(ProductJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

}







