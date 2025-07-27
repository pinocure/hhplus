package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * 역할: product 리포지토리 인터페이스 (out port)
 * 책임: Product 데이터 접근을 추상화하여 도메인과 외부 저장소 간 의존성을 분리
 */

public interface ProductRepository {

    List<Product> findAll();
    Optional<Product> findById(Long productId);
    List<Product> findPopular(int days, int limit);
    Product save(Product product);

    boolean checkProductVersion(Long productId, Long version);

}