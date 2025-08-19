package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.LockTimeoutException;

import java.util.List;

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
            throw new BusinessException(ErrorCode.PRODUCT_FINISH);
        }
        return products;
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
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
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            product.reserveStock(quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deductStock(Long productId, int quantity) {
        try {
            // 비관적 락으로 조회
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getStock() < quantity) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        }
    }

    @Override
    public void restoreStock(Long productId, int quantity) {

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void rollBackStock(Long productId, int quantity) {
        try {
            // 비관적 락으로 조회
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        }
    }

}











