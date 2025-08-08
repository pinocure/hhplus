package com.hhplus.ecommerce.order.adapter.out.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 역할: Product 서비스의 REST API를 호출하기 위한 Feign Client 인터페이스
 * 책임: HTTP 통신 세부사항을 선언적으로 정의하고 Product 서비스의 엔드포인트와 매핑
 */

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductFeignClient {

    @GetMapping("/products/{productId}")
    ProductDto getProduct(@PathVariable("productId") Long productId);

    @PostMapping("/products/{productId}/deduct")
    void deductStock(@PathVariable("productId") Long productId, @RequestParam("quantity") int quantity);

    @PostMapping("/products/{productId}/reserve")
    void reserveStock(@PathVariable("productId") Long productId, @RequestParam("quantity") int quantity);

    @PostMapping("/products/stock/confirm")
    void confirmStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

    @PostMapping("/products/{productId}/cancel-reservation")
    void cancelReservation(@PathVariable("productId") Long productId, @RequestParam("quantity") int quantity);

    @PostMapping("/products/stock/restore")
    void restoreStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);


    class ProductDto {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stock;

        // getter and setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

}
