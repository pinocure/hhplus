package com.hhplus.ecommerce.order;

import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class OrderServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("orders")
            .withUsername("ruang")
            .withPassword("ruang");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 생성, 저장")
    void orderRepositoryIntegrationTest() {
        // given : 주문 생성
        Order order = new Order(1L, new ArrayList<>(), new ArrayList<>());

        // when : 주문 저장
        Order saved = orderRepository.save(order);

        // then : 저장 성공
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    @DisplayName("주문 조회")
    void findOrderIntegrationTest() {
        // given : 주문 저장
        Order order = new Order(1L, new ArrayList<>(), new ArrayList<>());
        Order saved = orderRepository.save(order);

        // when : 주문 조회
        var found = orderRepository.findById(saved.getId());

        // then : 조회 성공
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getUserId());
    }

}









