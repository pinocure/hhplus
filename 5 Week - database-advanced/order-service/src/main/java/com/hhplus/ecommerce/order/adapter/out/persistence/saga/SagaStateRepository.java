package com.hhplus.ecommerce.order.adapter.out.persistence.saga;

import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 역할: Saga 상태 리포지토리
 * 책임: Saga 트랜잭션 상태의 영속화 및 조회를 담당
 */

@Repository
public interface SagaStateRepository extends JpaRepository<SagaStateJpaEntity, String> {

    default Optional<SagaTransaction> findSagaById(String sagaId) {
        return findById(sagaId).map(SagaStateJpaEntity::toDomain);
    }

    default SagaTransaction saveSaga(SagaTransaction saga) {
        SagaStateJpaEntity entity = SagaStateJpaEntity.from(saga);
        SagaStateJpaEntity saved = save(entity);
        return saved.toDomain();
    }

}







