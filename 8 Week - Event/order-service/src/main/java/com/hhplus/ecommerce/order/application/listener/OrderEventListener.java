package com.hhplus.ecommerce.order.application.listener;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class OrderEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            Thread.sleep(100);

            mockApiCall(event);
        } catch (Exception e) {
            handleFailure(event, e);
        }
    }

    private void mockApiCall(OrderCompletedEvent event) {

    }

    private void handleFailure(OrderCompletedEvent event, Exception e) {

    }

}









