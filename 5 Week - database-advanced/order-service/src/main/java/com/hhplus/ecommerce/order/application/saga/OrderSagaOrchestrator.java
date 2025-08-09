package com.hhplus.ecommerce.order.application.saga;

import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
            // 재고 예약
            order.getItems().forEach(item -> {
                productPort.reserveStock(item.getOrderProductId(), item.getQuantity());
            });
            orderSagaState.updateStep(sagaId, "STOCK_RESERVED");

            // 쿠폰 검증 및 사용
            order.getCoupons().forEach(coupon -> {
                couponPort.validateCoupon(coupon.getCouponCode());
                couponPort.useCoupon(coupon.getCouponCode());
            });
            orderSagaState.updateStep(sagaId, "COUPON_USED");

            // Saga 완료
            orderSagaState.complete(sagaId);
            order.confirm();

        } catch (Exception e) {
            compensateOrderSaga(sagaId, order);
            orderSagaState.fail(sagaId);
            throw new RuntimeException("주문 생성 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void startPaymentSaga(Order order) {
        String sagaId = generateSagaId();
        orderSagaState.create(sagaId, order.getId());

        try {
            // 잔액 차감
            BigDecimal totalAmount = order.getTotalPrice();
            balancePort.deductBalance(order.getUserId(), totalAmount);
            orderSagaState.updateStep(sagaId, "BALANCE_DEDUCTED");

            // 재고 확정
            order.getItems().forEach(item -> {
                productPort.confirmStock(item.getOrderProductId(), item.getQuantity());
            });
            orderSagaState.updateStep(sagaId, "STOCK_CONFIRMED");

            // Saga 완료
            orderSagaState.complete(sagaId);
            order.complete();

        } catch (Exception e) {
            compensatePaymentSaga(sagaId, order);
            orderSagaState.fail(sagaId);
            throw new RuntimeException("결제 처리 실패: " + e.getMessage(), e);
        }
    }

    private void compensateOrderSaga(String sagaId, Order order) {
        String lastStep = orderSagaState.getLastStep(sagaId);

        // 역순으로 보상 트랜잭션 실행
        if ("COUPON_USED".equals(lastStep) || "STOCK_RESERVED".equals(lastStep)) {
            // 쿠폰 사용 취소
            order.getCoupons().forEach(coupon -> {
                try {
                    couponPort.cancelCoupon(coupon.getCouponCode());
                } catch (Exception e) {
                    // 보상 실패 무시
                }
            });
        }

        if ("STOCK_RESERVED".equals(lastStep)) {
            // 재고 예약 취소
            order.getItems().forEach(item -> {
                try {
                    productPort.cancelReservation(item.getOrderProductId(), item.getQuantity());
                } catch (Exception e) {
                    // 보상 실패 무시
                }
            });
        }
    }

    private void compensatePaymentSaga(String sagaId, Order order) {
        String lastStep = orderSagaState.getLastStep(sagaId);

        // 역순으로 보상 트랜잭션 실행
        if ("STOCK_CONFIRMED".equals(lastStep) || "BALANCE_DEDUCTED".equals(lastStep)) {
            // 재고 복원
            order.getItems().forEach(item -> {
                try {
                    productPort.restoreStock(item.getOrderProductId(), item.getQuantity());
                } catch (Exception e) {
                    // 보상 실패 무시
                }
            });
        }

        if ("BALANCE_DEDUCTED".equals(lastStep)) {
            // 잔액 환불
            try {
                balancePort.refundBalance(order.getUserId(), order.getTotalPrice());
            } catch (Exception e) {
                // 보상 실패 무시
            }
        }
    }

    private String generateSagaId() {
        return "SAGA-" + UUID.randomUUID().toString();
    }

}







