package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.application.port.out.UserRepository;
import com.hhplus.ecommerce.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 역할: user 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: UserRepository를 구현하며, JPA 등 실제 DB와 연결하여 데이터 영속화
*/

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final Map<Long, User> store = new HashMap<>();

    public UserRepositoryAdapter() {
        store.put(1L, new User(1L));
    }


    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

}













