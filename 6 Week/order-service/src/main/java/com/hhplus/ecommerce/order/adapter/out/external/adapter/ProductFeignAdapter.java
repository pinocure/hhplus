package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.ProductFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 역할: Product 서비스와의 통신을 담당하는 Feign 기반 어댑터
 * 책임: Feign Client를 통해 Product 서비스의 기능을 호출
 */

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
}






