package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.adapter.out.persistence.BalanceJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 역할: Balance JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 잔액 데이터의 실제 DB 접근을 담당하며 비관적 락 지원
 */

public interface BalanceJpaRepository extends JpaRepository<BalanceJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BalanceJpaEntity b WHERE b.userId = :userId")
    Optional<BalanceJpaEntity> findByIdWithLock(@Param("userId") Long userId);

}
