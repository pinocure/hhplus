package com.hhplus.ecommerce.product.application.port.in;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;

/**
 * 역할: product 도메인의 입력 포트 인터페이스
 * 책임: 상품 관련 유즈케이스(조회, 인기 상품)를 정의하여 애플리케이션 서비스가 이를 구현하도록 함
 */

public interface ProductUseCase {

    List<Product> getAllProducts();                         // 전체 상품 조회
    Product getProduct(Long productId);                     // 상품 상세 조회
    List<Product> getPopularProducts(int days, int limit);  // 인기 상품 조회

    void deductStock(Long productId, int quantity);

}
