package com.hhplus.ecommerce.product.application.port.in;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;

/**
 * 역할: Product Query UseCase 인터페이스
 * 책임: 상품 조회 관련 비즈니스 로직을 정의 (CQRS Read Model)
 */

public interface ProductQueryUseCase {

    List<Product> getAllProducts();
    Product getProduct(Long productId);
    List<Product> getPopularProducts(int days, int limit);

}
