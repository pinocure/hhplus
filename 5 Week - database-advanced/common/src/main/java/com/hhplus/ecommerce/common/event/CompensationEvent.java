package com.hhplus.ecommerce.common.event;

import lombok.Getter;

/**
 * 역할: 보상 트랜잭션 이벤트
 * 책임: 분산 트랜잭션 실패 시 보상 작업이 필요함을 알리는 이벤트를 정의
 */

@Getter
public class CompensationEvent extends SagaEvent {

    private final String compensationType;
    private final String targetService;
    private final Object compensationData;

    public CompensationEvent(String sagaId, String compensationType, String targetService, Object compensationData) {
        super(sagaId, "COMPENSATION");
        this.compensationType = compensationType;
        this.targetService = targetService;
        this.compensationData = compensationData;
    }

}








