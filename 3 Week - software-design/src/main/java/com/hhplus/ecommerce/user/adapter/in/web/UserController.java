package com.hhplus.ecommerce.user.adapter.in.web;

import com.hhplus.ecommerce.user.application.port.in.UserUseCase;
import com.hhplus.ecommerce.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 역할: user 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: HTTP 요청을 처리하고, UserUseCase를 호출하여 유즈케이스 흐름을 트리거
*/

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userUseCase.getUser(userId);
        return ResponseEntity.ok(user);
    }

}












