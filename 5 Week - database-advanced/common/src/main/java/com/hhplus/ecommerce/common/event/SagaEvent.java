package com.hhplus.ecommerce.common.event;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 역할: Saga 이벤트 추상 클래스
 * 책임: 분산 트랜잭션 처리를 위한 Saga 이벤트의 공통 속성과 구조를 정의
 */

@Getter
public abstract class SagaEvent {

    private final String sagaId;
    private final String eventType;
    private final LocalDateTime timestamp;

    protected SagaEvent(String sagaId, String eventType) {
        this.sagaId = sagaId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

}









