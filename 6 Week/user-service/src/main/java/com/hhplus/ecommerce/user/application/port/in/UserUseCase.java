package com.hhplus.ecommerce.user.application.port.in;

import com.hhplus.ecommerce.user.domain.User;

public interface UserUseCase {

    User getUser(Long userId);

}
