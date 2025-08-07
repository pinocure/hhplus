package com.hhplus.ecommerce.product.adapter.out.persistence.command;

import com.hhplus.ecommerce.product.adapter.out.persistence.ProductJpaEntity;
import com.hhplus.ecommerce.product.application.port.out.ProductCommandRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductJpaRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 역할: Product Command 어댑터
 * 책임: 상품의 쓰기 작업을 위한 영속성 어댑터 (CQRS Write Model)
 */

@Component
public class ProductCommandAdapter implements ProductCommandRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductCommandAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void deleteById(Long productId) {
        productJpaRepository.deleteById(productId);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId).map(ProductJpaEntity::toDomain);
    }

}









