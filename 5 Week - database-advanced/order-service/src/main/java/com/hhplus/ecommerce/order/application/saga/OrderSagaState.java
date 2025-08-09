package com.hhplus.ecommerce.order.application.saga;

import com.hhplus.ecommerce.order.adapter.out.persistence.saga.SagaStateRepository;
import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        sagaStateRepository.saveSaga(saga);
    }

    public void updateStep(String sagaId, String step) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga not found: " + sagaId));
        saga.addStep(step);
        sagaStateRepository.saveSaga(saga);
    }

    public void complete(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga not found: " + sagaId));
        saga.complete();
        sagaStateRepository.saveSaga(saga);
    }

    public void fail(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga not found: " + sagaId));
        saga.fail();
        sagaStateRepository.saveSaga(saga);
    }

    public String getLastStep(String sagaId) {
        SagaTransaction saga = sagaStateRepository.findSagaById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga not found: " + sagaId));
        return saga.getCurrentStep();
    }

}








