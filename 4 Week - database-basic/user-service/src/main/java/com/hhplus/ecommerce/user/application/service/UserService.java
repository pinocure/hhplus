package com.hhplus.ecommerce.user.application.service;

import com.hhplus.ecommerce.user.application.port.in.UserUseCase;
import com.hhplus.ecommerce.user.application.port.out.UserRepository;
import com.hhplus.ecommerce.user.domain.User;
import org.springframework.stereotype.Service;

/**
 * 역할: user 서비스 구현 클래스
 * 책임: UserUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용
*/

@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}











