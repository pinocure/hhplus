package com.hhplus.ecommerce.order.adapter.out.persistence.saga;

import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 역할: Saga 상태 JPA 엔티티
 * 책임: Saga 트랜잭션 상태를 DB에 영속화하고 도메인 모델과 변환
 */

@Entity
@Table(name = "saga_state")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaStateJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String status;

    private String currentStep;

    @Column(columnDefinition = "TEXT")
    private String completedSteps;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    public SagaStateJpaEntity(String id,
                              Long orderId,
                              String status,
                              String currentStep,
                              String completedSteps,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.currentStep = currentStep;
        this.completedSteps = completedSteps;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static SagaStateJpaEntity from(SagaTransaction saga) {
        return new SagaStateJpaEntity(
                saga.getId(),
                saga.getOrderId(),
                saga.getStatus(),
                saga.getCurrentStep(),
                String.join(",", saga.getCompletedSteps()),
                saga.getCreatedAt(),
                saga.getUpdatedAt()
        );
    }

    public SagaTransaction toDomain() {
        SagaTransaction saga = new SagaTransaction(this.id, this.orderId);
        saga.setStatus(this.status);
        saga.setCurrentStep(this.currentStep);

        if (this.completedSteps != null && !this.completedSteps.isEmpty()) {
            for (String step : this.completedSteps.split(",")) {
                saga.addStep(step);
            }
        }

        saga.setCreatedAt(this.createdAt);
        saga.setUpdatedAt(this.updatedAt);
        return saga;
    }

}










