package com.hhplus.ecommerce.common.event;

import lombok.Getter;

/**
 * 역할: 주문 Saga 이벤트
 * 책임: 주문 처리 과정에서 발생하는 Saga 이벤트의 세부 정보를 정의
 */

@Getter
public class OrderSagaEvent extends SagaEvent {

    private final Long orderId;
    private final Long userId;
    private final String status;

    public OrderSagaEvent(String sagaId, String eventType, Long orderId, Long userId, String status) {
        super(sagaId, eventType);
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
    }

}









