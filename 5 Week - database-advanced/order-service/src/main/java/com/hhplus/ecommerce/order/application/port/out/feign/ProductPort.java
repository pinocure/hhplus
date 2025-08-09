package com.hhplus.ecommerce.order.application.port.out.feign;

import java.math.BigDecimal;

/**
 * 역할: Product 서비스와의 통신을 위한 출력 포트 인터페이스
 * 책임: 상품 조회 및 재고 차감 기능을 추상화하여 도메인이 외부 서비스 구현에 의존하지 않도록 함
 */

public interface ProductPort {

    ProductDto getProduct(Long productId);
    void deductStock(Long productId, int quantity);
    void reserveStock(Long productId, int quantity);
    void cancelReservation(Long productId, int quantity);
    void confirmStock(Long productId, int quantity);
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
