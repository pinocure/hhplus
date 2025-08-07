package com.hhplus.ecommerce.product.application.port.in;

/**
 * 역할: Product Command UseCase 인터페이스
 * 책임: 상품의 상태 변경 관련 비즈니스 로직을 정의 (CQRS Write Model)
 */

public interface ProductCommandUseCase {

    void deductStock(Long productId, int quantity);
    void reserveStock(Long productId, int quantity);
    void cancelReservation(Long productId, int quantity);

}
