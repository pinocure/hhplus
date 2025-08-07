package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * 역할: Product Query 리포지토리 인터페이스 (Read Model)
 * 책임: 상품 조회 작업을 위한 출력 포트를 정의하여 읽기 최적화된 쿼리를 지원하고 도메인과 조회 로직 간의 의존성을 분리
 */

public interface ProductQueryRepository {

    Optional<Product> findById(Long productId);
    List<Product> findAll();
    List<Product> findPopularProducts(int days, int limit);

}









