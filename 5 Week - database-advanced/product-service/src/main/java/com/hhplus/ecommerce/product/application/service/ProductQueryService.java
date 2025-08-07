package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.application.port.in.ProductQueryUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductQueryRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 역할: Product Query 서비스
 * 책임: 최적화된 읽기 모델을 통해 상품 조회 로직을 처리 (CQRS Read Model)
 */

@Service
public class ProductQueryService implements ProductQueryUseCase {

    private final ProductQueryRepository queryRepository;

    public ProductQueryService(ProductQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        return queryRepository.findAll();
    }

    @Override
    public Product getProduct(Long productId) {
        return queryRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("상품이 존재하지 않습니다."));
    }

    @Override
    public List<Product> getPopularProducts(int days, int limit) {
        return queryRepository.findPopularProducts(days, limit);
    }

}









