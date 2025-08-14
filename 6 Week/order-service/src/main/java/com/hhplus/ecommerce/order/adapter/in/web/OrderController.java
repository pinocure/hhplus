package com.hhplus.ecommerce.order.adapter.in.web;

import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long orderId) {
        Order order = orderUseCase.payOrder(orderId);
        return ResponseEntity.ok(order);
    }

}












