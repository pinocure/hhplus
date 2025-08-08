package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductCommandUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 역할: Product Command 컨트롤러
 * 책임: 상품의 상태 변경 요청(재고 차감, 예약 등)을 처리하는 Write 전용 API 엔드포인트
 */

@RestController
@RequestMapping("/products")
public class ProductCommandController {

    private final ProductCommandUseCase productCommandUseCase;

    public ProductCommandController(ProductCommandUseCase productCommandUseCase) {
        this.productCommandUseCase = productCommandUseCase;
    }

    @PostMapping("/{productId}/deduct")
    public ResponseEntity<Void> deductStock(@PathVariable Long productId, @RequestParam int quantity) {
        productCommandUseCase.deductStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserveStock(@PathVariable Long productId, @RequestParam int quantity) {
        productCommandUseCase.reserveStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/cancel-reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long productId, @RequestParam int quantity) {
        productCommandUseCase.cancelReservation(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stock/confirm")
    public ResponseEntity<Void> confirmStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        productCommandUseCase.deductStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stock/restore")
    public ResponseEntity<Void> restoreStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        productCommandUseCase.cancelReservation(productId, quantity);
        return ResponseEntity.ok().build();
    }

}







