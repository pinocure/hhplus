package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getAllMockOrders() {
        return orderService.getAllMockOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable long orderId) {
        return orderService.getMockOrderById(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getMockOrdersByUserId(@PathVariable long userId) {
        return orderService.getMockOrdersByUserId(userId);
    }

}
