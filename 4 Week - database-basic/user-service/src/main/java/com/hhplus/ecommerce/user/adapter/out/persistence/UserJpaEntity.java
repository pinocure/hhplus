package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 역할: User JPA 엔티티 클래스
 * 책임: 사용자 테이블과 매핑되는 JPA 엔티티로, DB 스키마와 도메인 모델 간의 변환을 담당
 */

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public UserJpaEntity(Long id) {
        this.id = id;
    }

    public static UserJpaEntity from(User user) {
        return new UserJpaEntity(user.getId());
    }

    public User toDomain() {
        return new User(this.id);
    }

}












