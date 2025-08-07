package com.hhplus.ecommerce.order.domain.saga;

import lombok.Getter;
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
public class SagaTransaction {

    private String id;
    private Long orderId;
    private String currentStep;
    private String status;          // STARTED, COMPENSATING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> completedSteps;

    public SagaTransaction(String id, Long orderId) {
        this.id = id;
        this.orderId = orderId;
        this.status = "STARTED";
        this.completedSteps = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addStep(String step) {
        this.completedSteps.add(step);
        this.currentStep = step;
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

    public void startCompensation() {
        this.status = "COMPENSATING";
        this.updatedAt = LocalDateTime.now();
    }

}








