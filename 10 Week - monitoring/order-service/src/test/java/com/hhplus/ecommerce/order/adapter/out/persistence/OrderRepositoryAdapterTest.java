package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest()
@Testcontainers
@Import(OrderRepositoryAdapter.class)
@ActiveProfiles("test-no-kafka")
public class OrderRepositoryAdapterTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("orders")
            .withUsername("ruang")
            .withPassword("ruang");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("app.redis.enabled", () -> "false");
    }

    @Autowired
    private OrderRepositoryAdapter repository;

    @Test
    void save_and_find() {
        Order order = new Order(1L, new ArrayList<>(), new ArrayList<>());
        Order saved = repository.save(order);

        assertNotNull(saved.getId());

        Optional<Order> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void find_not_found() {
        Optional<Order> found = repository.findById(987L);
        assertFalse(found.isPresent());
    }

    @Test
    void saveOrderProduct_success() {
        OrderProduct product = new OrderProduct(1L, "Test Product", BigDecimal.valueOf(1000));
        OrderProduct saved = repository.saveOrderProduct(product);

        assertNotNull(saved.getId());
        assertEquals("Test Product", saved.getName());

        Optional<OrderProduct> found = repository.findOrderProductById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

}





