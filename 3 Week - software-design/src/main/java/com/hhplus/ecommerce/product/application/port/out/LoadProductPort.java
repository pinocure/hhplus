package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * 역할: product 로드 관련 출력 포트 인터페이스
 * 책임: 상품 데이터 로드 메서드를 정의하여 외부 어댑터가 이를 구현하도록 추상화
 */

public interface LoadProductPort {

    List<Product> loadProducts();
    Optional<Product> loadProduct(Long productId);
    List<Product> loadPopularProducts(int days, int limit);

}
