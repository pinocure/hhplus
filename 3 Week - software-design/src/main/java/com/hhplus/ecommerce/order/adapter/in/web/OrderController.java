package com.hhplus.ecommerce.order.adapter.in.web;

import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 역할: order 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: 주문 관련 HTTP 요청을 처리하고, OrderUseCase를 호출하여 유즈케이스 흐름을 트리거
 */

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long userId,
                                             @RequestParam List<Long> productIds,
                                             @RequestParam List<Integer> quantities,
                                             @RequestParam(required = false) List<String> couponCodes) {
        Order order = orderUseCase.createOrder(userId, productIds, quantities, couponCodes != null ? couponCodes : List.of());

        return ResponseEntity.ok(order);
    }

}












