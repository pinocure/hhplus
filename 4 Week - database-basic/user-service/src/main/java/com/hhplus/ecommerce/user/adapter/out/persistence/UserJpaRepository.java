package com.hhplus.ecommerce.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 역할: User JPA Repository 인터페이스
 * 책임: Spring Data JPA를 통해 사용자 데이터의 실제 DB 접근을 담당
 */

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {



}
