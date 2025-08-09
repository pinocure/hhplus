package com.hhplus.ecommerce.order.adapter.out.persistence.saga;

import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 역할: Saga 상태 JPA 엔티티
 * 책임: Saga 트랜잭션 상태를 데이터베이스에 영속화
 */

@Entity
@Table(name = "saga_state")
@Getter
@Setter
@NoArgsConstructor
public class SagaStateJpaEntity {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_step", length = 100)
    private String currentStep;

    @Column(name = "completed_steps", columnDefinition = "TEXT")
    private String completedSteps;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static SagaStateJpaEntity from(SagaTransaction saga) {
        SagaStateJpaEntity entity = new SagaStateJpaEntity();
        entity.id = saga.getSagaId();
        entity.orderId = saga.getOrderId();
        entity.status = saga.getStatus();
        entity.currentStep = saga.getCurrentStep();
        entity.completedSteps = saga.getCompletedSteps();
        entity.createdAt = saga.getCreatedAt();
        entity.updatedAt = saga.getUpdatedAt();
        return entity;
    }

    public SagaTransaction toDomain() {
        SagaTransaction saga = new SagaTransaction(id, orderId);
        saga.setStatus(status);
        saga.setCurrentStep(currentStep);
        saga.setCompletedSteps(completedSteps);
        saga.setCreatedAt(createdAt);
        saga.setUpdatedAt(updatedAt);
        return saga;
    }

}










