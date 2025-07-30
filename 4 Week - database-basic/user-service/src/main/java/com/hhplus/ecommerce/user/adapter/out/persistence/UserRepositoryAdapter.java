package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.application.port.out.UserRepository;
import com.hhplus.ecommerce.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 역할: user 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: UserRepository를 구현하며, JPA 등 실제 DB와 연결하여 데이터 영속화 및 도메인-JPA 엔티티 간 변환
*/

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private  final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return saved.toDomain();
    }

}













