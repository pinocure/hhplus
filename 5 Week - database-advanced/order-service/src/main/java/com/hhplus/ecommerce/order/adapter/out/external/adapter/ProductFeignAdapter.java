package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.ProductFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import feign.FeignException;
import org.springframework.stereotype.Component;

/**
 * 역할: ProductPort의 구현체로서 Feign Client를 사용한 Product 서비스 통신 어댑터
 * 책임: Feign Client 호출, 예외 처리, 데이터 변환 등 기술적 세부사항을 처리
 */

@Component
public class ProductFeignAdapter implements ProductPort {

    private final ProductFeignClient productFeignClient;

    public ProductFeignAdapter(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }


    @Override
    public ProductDto getProduct(Long productId) {
        try {
            ProductFeignClient.ProductDto feignDto = productFeignClient.getProduct(productId);

            ProductDto portDto = new ProductDto();
            portDto.setId(productId);
            portDto.setName(feignDto.getName());
            portDto.setPrice(feignDto.getPrice());
            portDto.setStock(feignDto.getStock());

            return portDto;
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
    public void reserveStock(Long productId, int quantity) {
        try {
            productFeignClient.reserveStock(productId, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("재고 예약 실패 : " + e.getMessage());
        }
    }

    @Override
    public void cancelReservation(Long productId, int quantity) {
        try {
            productFeignClient.cancelReservation(productId, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("재고 예약 취소 실패 : " + e.getMessage());
        }
    }

}







