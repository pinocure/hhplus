package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.domain.Product;

import java.util.Optional;

/**
 * 역할: Product Command 리포지토리 인터페이스 (Write Model)
 * 책임: 상품의 상태 변경(생성, 수정, 삭제) 작업을 위한 출력 포트를 정의하여 도메인과 영속성 계층 간의 의존성을 분리
 */

public interface ProductCommandRepository {

    Product save(Product product);
    void deleteById(Long productId);
    Optional<Product> findById(Long productId);

}









