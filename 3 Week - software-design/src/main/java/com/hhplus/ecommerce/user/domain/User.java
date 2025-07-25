package com.hhplus.ecommerce.user.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 역할: user 도메인 엔티티 클래스
 * 책임: 사용자 속성(id 등)과 도메인 로직을 캡슐화하여 비즈니스 규칙을 유지
*/

@Getter
@Setter
public class User {

    private Long id;

    public User(Long id) {
        this.id = id;
    }
}











