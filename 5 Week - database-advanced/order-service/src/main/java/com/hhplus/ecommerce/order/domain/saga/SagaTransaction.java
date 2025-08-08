package com.hhplus.ecommerce.order.domain.saga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 역할: Saga Transaction 도메인 엔티티
 * 책임: 분산 트랜잭션의 상태와 진행 단계를 추적하고 상태 전이 규칙을 관리
 */

@Getter
@Setter
@NoArgsConstructor
public class SagaTransaction {

    private String sagaId;
    private Long orderId;
    private String status; // STARTED, COMPLETED, FAILED
    private String currentStep;
    private String completedSteps;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SagaTransaction(String sagaId, Long orderId) {
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.status = "STARTED";
        this.currentStep = "INIT";
        this.completedSteps = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addStep(String step) {
        this.currentStep = step;
        if (this.completedSteps == null || this.completedSteps.isEmpty()) {
            this.completedSteps = step;
        } else {
            this.completedSteps = this.completedSteps + "," + step;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = "FAILED";
        this.updatedAt = LocalDateTime.now();
    }

    public String getCurrentStep() {
        return currentStep;
    }

}








