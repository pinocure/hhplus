package com.hhplus.ecommerce.user.application.port.out;

import com.hhplus.ecommerce.user.adapter.out.persistence.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {



}
