package com.hhplus.ecommerce.product.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 역할: Product JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 상품 데이터의 실제 DB 접근을 담당
 */

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    @Query("SELECT p FROM ProductJpaEntity p ORDER BY p.stock ASC")
    List<ProductJpaEntity> findPopularProductsOrderByStock(@Param("limit") int limit);

}
