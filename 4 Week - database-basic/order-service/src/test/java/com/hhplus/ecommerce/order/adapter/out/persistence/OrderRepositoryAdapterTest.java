package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryAdapterTest {

    private final OrderRepository repository = new OrderRepositoryAdapter();

    @Test
    void save_and_find() {
        Order order = new Order(1L, List.of(), List.of());
        Order saved = repository.save(order);

        assertNotNull(saved.getId());
        Optional<Order> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void find_not_found() {
        Optional<Order> found = repository.findById(2L);
        assertFalse(found.isPresent());
    }

}









