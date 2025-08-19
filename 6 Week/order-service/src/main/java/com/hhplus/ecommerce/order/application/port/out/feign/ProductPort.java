package com.hhplus.ecommerce.order.application.port.out.feign;

import java.math.BigDecimal;

public interface ProductPort {

    ProductDto getProduct(Long productId);
    void deductStock(Long productId, int quantity);
    void restoreStock(Long productId, int quantity);

    // DTO 정의
    class ProductDto {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stock;

        public ProductDto() {}

        public ProductDto(Long id, String name, BigDecimal price, Integer stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        // getter & setter
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
