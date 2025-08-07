package com.hhplus.ecommerce.order.application.saga;

import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 역할: Order Saga Orchestrator
 * 책임: 주문 처리의 분산 트랜잭션을 조율하고, 실패 시 보상 트랜잭션을 실행
 */

@Component
public class OrderSagaOrchestrator {

    private final BalancePort balancePort;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final OrderSagaState orderSagaState;

    public OrderSagaOrchestrator(BalancePort balancePort, ProductPort productPort, CouponPort couponPort, OrderSagaState orderSagaState) {
        this.balancePort = balancePort;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.orderSagaState = orderSagaState;
    }

    @Transactional
    public void startOrderSaga(Order order) {
        String sagaId = generateSagaId();
        orderSagaState.create(sagaId, order.getId());

        try {
            // 1. 재고 예약
            order.getItems().forEach(item -> {
                productPort.reserveStock(item.getOrderProductId(), item.getQuantity());
            });
            orderSagaState.updateStep(sagaId, "STOCK_RESERVED");

            // 2. 쿠폰 검증
            order.getCoupons().forEach(coupon -> {
                couponPort.validateCoupon(coupon.getCouponCode());
            });
            orderSagaState.updateStep(sagaId, "COUPON_VALIDATED");

            order.confirm();
            orderSagaState.complete(sagaId);
        } catch (Exception e) {
            // 보상 트랜잭션 실행
            compensate(sagaId, order);
            throw new RuntimeException("주문 처리 실패", e);
        }
    }

    private void compensate(String sagaId, Order order) {
        // 역순으로 보상트랜잭션 실행
        String lastStep = orderSagaState.getLastStep(sagaId);

        if ("COUPON_VALIDATED".equals(lastStep)) {
            // 쿠폰 사용 취소
            order.getCoupons().forEach(coupon -> {
                try {
                    couponPort.cancelCoupon(coupon.getCouponCode());
                } catch (Exception e) {
                    // 보상 트랜잭션 실패 로깅
                }
            });
        }

        if ("STOCK_RESERVED".equals(lastStep) || "COUPON_VALIDATED".equals(lastStep)) {
            // 재고 예약 취소
            order.getItems().forEach(item -> {
                try {
                    productPort.cancelReservation(item.getOrderProductId(), item.getQuantity());
                } catch (Exception e) {
                    // 보상 트랜잭션 실패 로깅
                }
            });
        }

        orderSagaState.fail(sagaId);
    }

    private String generateSagaId() {
        return "SAGA - " + UUID.randomUUID().toString();
    }

}







