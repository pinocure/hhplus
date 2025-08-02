package com.hhplus.ecommerce.balance.application.port.out;

import com.hhplus.ecommerce.balance.adapter.out.persistence.BalanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 역할: Balance JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 잔액 데이터의 실제 DB 접근을 담당
 */

public interface BalanceJpaRepository extends JpaRepository<BalanceJpaEntity, Long> {



}
