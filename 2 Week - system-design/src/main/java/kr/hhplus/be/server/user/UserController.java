package kr.hhplus.be.server.user;

import kr.hhplus.be.server.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllMockUsers() {
        return userService.getAllMockUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getMockUserById(@PathVariable Long userId) {
        return userService.getMockUserById(userId);
    }

}
