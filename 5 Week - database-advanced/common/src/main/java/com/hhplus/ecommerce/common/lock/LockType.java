package com.hhplus.ecommerce.common.lock;

/**
 * 역할: 락 타입 열거형
 * 책임: 동시성 제어에 사용되는 락의 종류를 정의
 */

public enum LockType {

    OPTIMISTIC,     // 낙관적 락
    PESSIMISTIC,    // 비관적 락
    DISTRIBUTED     // 분산 락 (Redis 등)

}








