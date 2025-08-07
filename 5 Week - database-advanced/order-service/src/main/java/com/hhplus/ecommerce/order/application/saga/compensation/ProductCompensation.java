package com.hhplus.ecommerce.order.application.saga.compensation;

import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import org.springframework.stereotype.Component;

/**
 * 역할: Product 서비스 보상 트랜잭션 처리기
 * 책임: 주문 실패 시 예약된 재고를 원복하는 보상 트랜잭션을 실행
 */

@Component
public class ProductCompensation {

    private final ProductPort productPort;

    public ProductCompensation(ProductPort productPort) {
        this.productPort = productPort;
    }

    public void compensate(Long productId, int quantity) {
        try {
            productPort.cancelReservation(productId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Product 보상 실패", e);
        }
    }

}






