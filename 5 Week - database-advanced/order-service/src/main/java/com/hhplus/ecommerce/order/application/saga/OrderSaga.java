package com.hhplus.ecommerce.order.application.saga;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 역할: Order Saga 상태 관리 클래스
 * 책임: 주문 처리 프로세스의 분산 트랜잭션 상태를 추적하고 관리
 */

@Getter
@Setter
public class OrderSaga {

    private String sagaId;
    private Long orderId;
    private String currentStep;
    private String status;          // STARTED, COMPENSATING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> completedSteps = new ArrayList<>();


    public OrderSaga(String sagaId, Long orderId) {
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.status = "STARTED";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addCompletedStep(String step) {
        this.completedSteps.add(step);
        this.currentStep = step;
        this.updatedAt = LocalDateTime.now();
    }

}








