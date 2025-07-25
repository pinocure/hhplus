package com.hhplus.ecommerce.user.application.port.out;

import com.hhplus.ecommerce.user.domain.User;

import java.util.Optional;

/**
 * 역할: user 로드 관련 출력 포트 인터페이스
 * 책임: 사용자 데이터 로드 메서드를 정의하여 외부 어댑터가 이를 구현하도록 추상화
*/

public interface LoadUserPort {

    Optional<User> loadUser(String username);

}
