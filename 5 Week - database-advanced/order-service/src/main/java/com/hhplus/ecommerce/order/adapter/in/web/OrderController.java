package com.hhplus.ecommerce.order.adapter.in.web;

import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 역할: order 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: 주문 관련 HTTP 요청을 처리하고, OrderUseCase를 호출하여 유즈케이스 흐름을 트리거 Saga 패턴이 적용된 주문 생성 및 결제 처리
 */

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }


    // 주문 생성 - Saga 패턴 적용 재고 예약, 쿠폰 사용 등의 분산 트랜잭션 처리
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long userId,
                                             @RequestParam List<Long> productIds,
                                             @RequestParam List<Integer> quantities,
                                             @RequestParam(required = false) List<String> couponCodes) {
        try {
            Order order = orderUseCase.createOrder(userId, productIds, quantities, couponCodes != null ? couponCodes : List.of());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 주문 결제 - Saga 패턴 적용 잔액 차감, 재고 확정 등의 분산 트랜잭션 처리
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long orderId) {
        try {
            Order order = orderUseCase.payOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        // 향후 구현 예정 - 조회는 saga 패턴 불필요
        return ResponseEntity.notFound().build();
    }

}












