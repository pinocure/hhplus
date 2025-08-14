package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.adapter.out.persistence.ProductJpaEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    @Query("SELECT p FROM ProductJpaEntity p ORDER BY p.stock ASC")
    List<ProductJpaEntity> findPopularProductsOrderByStock(@Param("limit") int limit);

    // 비관적 락, 타임아웃 3초
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.id = :productId")
    Optional<ProductJpaEntity> findByIdWithLock(@Param("productId") Long productId);

}
