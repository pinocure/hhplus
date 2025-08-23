package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.common.lock.DistributedLock;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

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
        // 먼저 레디스 확인 후 DB확인
        if (redisTemplate != null) {
            try {
                String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                String key = "product:ranking:" + today;

                Set<ZSetOperations.TypedTuple<String>> topProducts = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

                if (topProducts != null && !topProducts.isEmpty()) {
                    List<Product> rankedProducts = new ArrayList<>();
                    for (ZSetOperations.TypedTuple<String> tuple : topProducts) {
                        if (tuple.getValue() != null) {
                            Long productId = Long.parseLong(tuple.getValue());
                            productRepository.findById(productId).ifPresent(rankedProducts::add);
                        }
                    }
                    if (!rankedProducts.isEmpty()) {
                        return rankedProducts;
                    }
                }
            } catch (Exception e) {
                // 오류 발생하면 기존 로직으로 폴백
            }
        }

        List<Product> popular = productRepository.findPopular(days, limit);
        return popular.isEmpty() ? List.of() : popular;
    }

    @Transactional
    @DistributedLock(key = "lock:product:stock:#={p0}", waitTime = 3, leaseTime = 2)
    public void reserveStock(Long productId, int quantity, Long version) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.reserveStock(quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    @DistributedLock(key = "lock:product:stock:#={p0}", waitTime = 3, leaseTime = 2)
    public void deductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStock() < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        updateProductRanking(productId, quantity);
    }

    @Override
    @Transactional
    @DistributedLock(key = "lock:product:stock:#={p0}", waitTime = 3, leaseTime = 2)
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStock() < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    private void updateProductRanking(Long productId, int quantity) {
        if (redisTemplate == null) {
            return;
        }

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String key = "product:ranking:" + today;

            redisTemplate.opsForZSet().incrementScore(key, productId.toString(), quantity);

            redisTemplate.expire(key, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            // 실패해도 주문 계속 진행
        }
    }

}











