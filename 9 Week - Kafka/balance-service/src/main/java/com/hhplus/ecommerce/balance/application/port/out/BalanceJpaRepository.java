package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.adapter.out.persistence.BalanceJpaEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<BalanceJpaEntity, Long> {

    // 비관적 락, 타임아웃 3초
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT b FROM BalanceJpaEntity b WHERE b.userId = :userId")
    Optional<BalanceJpaEntity> findByUserIdWithLock(@Param("userId") Long userId);

}
