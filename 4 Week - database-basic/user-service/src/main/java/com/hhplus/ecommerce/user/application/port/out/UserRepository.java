package com.hhplus.ecommerce.user.application.port.out;

import com.hhplus.ecommerce.user.domain.User;

import java.util.Optional;

/**
 * 역할: user 리포지토리 인터페이스 (out port)
 * 책임: user 데이터 접근을 추상화하여 도메인과 외부 저장소 간 의존성을 분리
*/

public interface UserRepository {

    Optional<User> findById(Long userId);
    User save(User user);

}
