package com.hhplus.ecommerce.product.application.port.in;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.List;

public interface ProductUseCase {

    List<Product> getAllProducts();                         // 전체 상품 조회
    Product getProduct(Long productId);                     // 상품 상세 조회
    List<Product> getPopularProducts(int days, int limit);  // 인기 상품 조회

    void deductStock(Long productId, int quantity);
    void restoreStock(Long productId, int quantity);

}
