package com.hhplus.ecommerce.user.application.port.in;

import com.hhplus.ecommerce.user.domain.User;

/**
 * 역할: user 도메인의 입력 포트 인터페이스
 * 책임: user 관련 유즈케이스(예: 사용자 생성, 조회)를 정의하여 애플리케이션 서비스가 이를 구현하도록 함
*/

public interface UserUseCase {

    User getUser(Long userId);

}
