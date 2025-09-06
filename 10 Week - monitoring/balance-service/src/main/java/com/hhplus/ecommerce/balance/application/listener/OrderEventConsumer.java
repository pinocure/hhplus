package com.hhplus.ecommerce.balance.application.listener;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.event.OrderPaymentRequestEvent;
import com.hhplus.ecommerce.common.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final BalanceUseCase balanceUseCase;
    private final BalanceRepository balanceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-payment-request", groupId = "balance-service-group")
    @Transactional
    public void handlePaymentRequest(OrderPaymentRequestEvent event) {
        try {
            balanceUseCase.deductBalance(event.getUserId(), event.getTotalAmount());

            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getTotalAmount(),
                    true,
                    null,
                    LocalDateTime.now()
            );

            kafkaTemplate.send("balance-payment-completed", completedEvent);
        } catch (Exception e) {
            PaymentCompletedEvent failedEvent = new PaymentCompletedEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getTotalAmount(),
                    false, // 실패
                    e.getMessage(),
                    LocalDateTime.now()
            );

            kafkaTemplate.send("balance-payment-completed", failedEvent);
        }
    }


    @KafkaListener(topics = "order-completed", groupId = "balance-history-group")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            Balance currentBalance = balanceRepository.findByUserId(event.getUserId())
                    .orElse(null);

            if (currentBalance != null) {
                balanceRepository.recordBalanceUsage(
                        event.getUserId(),
                        event.getOrderId(),
                        event.getTotalPrice(),
                        currentBalance.getAmount(),
                        LocalDateTime.now()
                );
            }
        } catch (Exception e) {
            log.error("잔액 내역 기록 실패: {}", e.getMessage());
        }
    }

}









