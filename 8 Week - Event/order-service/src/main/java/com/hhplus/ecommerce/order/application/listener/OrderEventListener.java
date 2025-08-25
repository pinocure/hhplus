package com.hhplus.ecommerce.order.application.listener;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class OrderEventListener {

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            log.info("=== 주문 완료 이벤트 수신 ===");
            log.info("주문 ID: {}", event.getOrderId());
            log.info("사용자 ID: {}", event.getUserId());
            log.info("주문 금액: {}", event.getTotalPrice());
            log.info("주문 시간: {}", event.getOrderedAt());

            Thread.sleep(100);
            mockApiCall(event);
            log.info("데이터 플랫폼 전송 완료: orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("주문 완료 이벤트 처리 실패: orderId={}, error={}", event.getOrderId(), e.getMessage());
            handleFailure(event, e);
        }
    }

    private void mockApiCall(OrderCompletedEvent event) {
        log.info("📡 [Mock API] 데이터 플랫폼으로 주문 정보 전송 중...");
        log.info("📡 [Mock API] Endpoint: http://mock-data-platform.com/api/orders");
        log.info("📡 [Mock API] Request Body: {{\"orderId\":{}, \"userId\":{}, \"totalPrice\":{}, \"eventType\":\"ORDER_COMPLETED\"}}",
                event.getOrderId(), event.getUserId(), event.getTotalPrice());

        // HTTP 호출 시뮬레이션 (실제로는 호출하지 안함 -> mock이 목적)
        try {
            Thread.sleep(50); // 네트워크 지연 시뮬레이션
            log.info("📡 [Mock API] Response: {{\"status\":\"success\", \"messageId\":\"msg_{}\", \"timestamp\":\"{}\"}}",
                    UUID.randomUUID().toString().substring(0, 8), LocalDateTime.now());
            log.info("📡 [Mock API] 전송 성공!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Mock API 호출 중단", e);
        }
    }

    private void handleFailure(OrderCompletedEvent event, Exception e) {
        // 실패 처리 시뮬레이션
        log.error("[실패 처리] 주문 완료 이벤트 처리 실패");
        log.error("[실패 처리] 주문 ID: {}", event.getOrderId());
        log.error("[실패 처리] 실패 사유: {}", e.getMessage());
        log.error("[실패 처리] 실제 환경에서는 재시도 큐에 저장하거나 알림 발송");

        // Mock: 실패한 이벤트 정보를 로그로 기록
        log.warn("🔄 [재처리 필요] 수동 재처리가 필요한 이벤트 - EventId: event_{}_{}",
                event.getOrderId(), System.currentTimeMillis());
    }

}









