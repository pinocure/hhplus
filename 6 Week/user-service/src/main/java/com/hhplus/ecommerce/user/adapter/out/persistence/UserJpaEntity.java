package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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












