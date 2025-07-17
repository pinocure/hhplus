package kr.hhplus.be.server.user;

import kr.hhplus.be.server.user.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    public List<UserResponse> getAllMockUsers() {
        return List.of(
                new UserResponse(1L, "김철수", "kim@example.com", new BigDecimal("10000")),
                new UserResponse(2L, "이영희", "lee@example.com", new BigDecimal("20000")),
                new UserResponse(3L, "박민수", "park@example.com", new BigDecimal("15000"))
        );
    }

    public UserResponse getMockUserById(Long userId) {
        return new UserResponse(userId, "신짱구", "sin@example.com", new BigDecimal("30000"));
    }

}
