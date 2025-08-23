package com.hhplus.ecommerce.order.adapter.out.external.client;

import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;


@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductFeignClient {

    @GetMapping("/products/{productId}")
    ProductPort.ProductDto getProduct(@PathVariable("productId") Long productId);

    @PostMapping("/products/{productId}/deduct")
    void deductStock(@PathVariable("productId") Long productId,
                     @RequestParam("quantity") int quantity);

    @PostMapping("/products/{productId}/restore")
    void restoreStock(@PathVariable("productId") Long ProductId,
                      @RequestParam("quantity") int quantity);
}
