package com.hhplus.ecommerce.order.application.saga;

import com.hhplus.ecommerce.order.adapter.out.persistence.saga.SagaStateRepository;
import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import org.springframework.stereotype.Component;

/**
 * 역할: Saga 상태 관리 서비스
 * 책임: Saga의 진행 상태를 저장하고 관리하며, 각 단계별 상태 전이를 처리
 */

@Component
public class OrderSagaState {

    private final SagaStateRepository sagaStateRepository;

    public OrderSagaState(final SagaStateRepository sagaStateRepository) {
        this.sagaStateRepository = sagaStateRepository;
    }

    public void create(String sagaId, Long orderId) {
        SagaTransaction saga = new SagaTransaction(sagaId, orderId);
        sagaStateRepository.save(saga);
    }

    public void updateStep(String sagaId, String step) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga update error : " + sagaId));
        saga.addStep(step);
        sagaStateRepository.save(saga);
    }

    public void complete(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga complete error : " + sagaId));
        saga.complete();
        sagaStateRepository.save(saga);
    }

    public void fail(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga fail error : " + sagaId));
        saga.fail();
        sagaStateRepository.save(saga);
    }

    public String getLastStep(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga last error : " + sagaId));
        return saga.getCurrentStep();
    }

}








