package com.hhplus.ecommerce.order.adapter.out.external;

import com.hhplus.ecommerce.order.application.port.out.ProductPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 역할: Product 서비스와의 통신을 담당하는 외부 어댑터
 * 책임: HTTP 통신을 통해 Product 서비스의 기능을 호출
 */

@Component
public class ProductClientAdapter implements ProductPort {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClientAdapter(RestTemplate restTemplate,
                                @Value("${services.product.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public ProductPort.ProductDto getProduct(Long productId) {
        try {
            return restTemplate.getForObject(productServiceUrl + "/products/" + productId, ProductPort.ProductDto.class);
        } catch (Exception e) {
            throw new RuntimeException("상품 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void deductStock(Long productId, int quantity) {
        try {
            restTemplate.postForObject(productServiceUrl + "/products/" + productId + "/deduct?quantity=" + quantity, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("재고 차감 실패 : " + e.getMessage());
        }
    }

}











