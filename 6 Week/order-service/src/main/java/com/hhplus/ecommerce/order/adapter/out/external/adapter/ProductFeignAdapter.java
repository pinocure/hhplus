package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.ProductFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductPort {

    private final ProductFeignClient productFeignClient;

    @Override
    public ProductDto getProduct(Long productId) {
        try {
            return productFeignClient.getProduct(productId);
        } catch (FeignException e) {
            throw new RuntimeException("상품 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void deductStock(Long productId, int quantity) {
        try {
            productFeignClient.deductStock(productId, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("재고 차감 실패 : " + e.getMessage());
        }
    }

    @Override
    public void restoreStock(Long productId, int quantity) {
        try {
            productFeignClient.restoreStock(productId, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("재고 복구 실패 : " + e.getMessage());
        }
    }
}






