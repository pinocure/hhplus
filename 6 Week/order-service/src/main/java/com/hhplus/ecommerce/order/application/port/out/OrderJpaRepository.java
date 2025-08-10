package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderJpaEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 역할: Order JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 주문 데이터의 실제 DB 접근을 담당
 */

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    /**
     * 비관적 락을 사용하여 주문 조회
     * 결제 처리 시 동시성 제어를 위해 사용
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :orderId")
    Optional<OrderJpaEntity> findByIdWithPessimisticLock(@Param("orderId") Long orderId);

    /**
     * 낙관적 락을 사용하여 주문 조회
     * 일반적인 조회 시 사용
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :orderId")
    Optional<OrderJpaEntity> findByIdWithOptimisticLock(@Param("orderId") Long orderId);

}
