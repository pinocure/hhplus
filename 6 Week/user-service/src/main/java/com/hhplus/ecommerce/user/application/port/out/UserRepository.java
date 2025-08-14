package com.hhplus.ecommerce.user.application.port.out;

import com.hhplus.ecommerce.user.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long userId);
    User save(User user);

}
