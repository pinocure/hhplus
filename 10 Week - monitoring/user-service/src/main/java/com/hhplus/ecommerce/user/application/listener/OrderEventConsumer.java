package com.hhplus.ecommerce.user.application.listener;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.event.UserValidationCompletedEvent;
import com.hhplus.ecommerce.common.event.UserValidationRequestEvent;
import com.hhplus.ecommerce.user.application.port.in.UserUseCase;
import com.hhplus.ecommerce.user.application.port.out.UserRepository;
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

    private final UserUseCase userUseCase;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "user-validation-request", groupId = "user-service-group")
    @Transactional
    public void handleUserValidation(UserValidationRequestEvent event) {
        try {
            userUseCase.getUser(event.getUserId());

            UserValidationCompletedEvent successEvent = new UserValidationCompletedEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    true,
                    null,
                    LocalDateTime.now()
            );

            kafkaTemplate.send("user-validation-completed", successEvent);
        } catch (Exception e) {
            UserValidationCompletedEvent failedEvent = new UserValidationCompletedEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    false,
                    e.getMessage(),
                    LocalDateTime.now()
            );

            kafkaTemplate.send("user-validation-completed", failedEvent);
        }
    }

    @KafkaListener(topics = "order-completed", groupId = "user-purchase-group")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            userRepository.recordPurchaseHistory(event.getUserId(), event.getOrderId(), event.getTotalPrice());
        } catch (Exception e) {
            log.error("구매 이력 기록 실패: orderId={}, userId={}, error={}",
                    event.getOrderId(), event.getUserId(), e.getMessage());
        }
    }

}









