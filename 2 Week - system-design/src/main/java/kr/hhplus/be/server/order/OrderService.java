package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.dto.OrderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    public List<OrderResponse> getAllMockOrders() {
        return List.of(
                new OrderResponse(1L, 1L, 1L, "아이폰", 1, new BigDecimal("1000"),
                        new BigDecimal("1000"), "COMPLETED", LocalDateTime.now().minusDays(1)),
                new OrderResponse(2L, 2L, 2L, "갤럭시", 2, new BigDecimal("2000"),
                        new BigDecimal("4000"), "PENDING", LocalDateTime.now().minusHours(2)),
                new OrderResponse(3L, 1L, 3L, "갤럭시S24", 1, new BigDecimal("5000"),
                        new BigDecimal("5000"), "COMPLETED", LocalDateTime.now().minusHours(1))
        );
    }

    public List<OrderResponse> getMockOrdersByUserId(Long userId) {
        return List.of(
                new OrderResponse(1L, userId, 1L, "아이폰", 1, new BigDecimal("1000"),
                        new BigDecimal("1000"), "COMPLETED", LocalDateTime.now().minusDays(1)),
                new OrderResponse(3L, userId, 3L, "갤럭시S24", 1, new BigDecimal("5000"),
                        new BigDecimal("5000"), "COMPLETED", LocalDateTime.now().minusHours(1))
        );
    }

    public OrderResponse getMockOrderById(Long orderId) {
        return new OrderResponse(orderId, 1L, 1L, "아이폰", 1, new BigDecimal("1000"),
                new BigDecimal("1000"), "COMPLETED", LocalDateTime.now().minusDays(1));
    }

}
