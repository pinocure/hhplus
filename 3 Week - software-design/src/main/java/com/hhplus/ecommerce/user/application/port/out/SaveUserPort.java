package com.hhplus.ecommerce.user.application.port.out;

import com.hhplus.ecommerce.user.domain.User;

/**
 * 역할: user 저장 관련 출력 포트 인터페이스
 * 책임: 사용자 데이터 저장/업데이트 메서드를 정의하여 외부 어댑터가 이를 구현하도록 추상화
*/

public interface SaveUserPort {

    User saveUser(User user);

}
