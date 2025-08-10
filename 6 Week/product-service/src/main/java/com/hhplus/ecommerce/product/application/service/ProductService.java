package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.LockTimeoutException;

import java.util.List;

/**
 * 역할: product 서비스 구현 클래스
 * 책임: ProductUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용
 */

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new IllegalStateException("상품이 품절되었습니다.");
        }
        return products;
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }

    @Override
    public List<Product> getPopularProducts(int days, int limit) {
        List<Product> popular = productRepository.findPopular(days, limit);

        return popular.isEmpty() ? List.of() : popular;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void reserveStock(Long productId, int quantity, Long version) {
        try {
            // 비관적 락으로 조회
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
            product.reserveStock(quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalStateException("재고 처리 중 잠금 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deductStock(Long productId, int quantity) {
        try {
            // 비관적 락으로 조회
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
            product.deductStock(quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalStateException("재고 처리 중 잠금 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void rollBackStock(Long productId, int quantity) {
        try {
            // 비관적 락으로 조회
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
            product.rollbackReservedStock(quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new IllegalStateException("재고 롤백 중 잠금 오류가 발생했습니다.", e);
        }
    }

}











