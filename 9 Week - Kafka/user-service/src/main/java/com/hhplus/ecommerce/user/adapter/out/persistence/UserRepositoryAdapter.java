package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.application.port.out.UserJpaRepository;
import com.hhplus.ecommerce.user.application.port.out.UserRepository;
import com.hhplus.ecommerce.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Override
    public void recordPurchaseHistory(Long userId, Long orderId, BigDecimal amount) {
        System.out.println("구매 이력 기록: userId=" + userId +
                ", orderId=" + orderId +
                ", amount=" + amount);
    }

}













