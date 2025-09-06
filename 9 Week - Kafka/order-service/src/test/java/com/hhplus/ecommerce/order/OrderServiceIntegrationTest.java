package com.hhplus.ecommerce.order;

import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.redis.redisson.config=",
        "spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2"
})
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
})
public class OrderServiceIntegrationTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 생성, 저장")
    void orderRepositoryIntegrationTest() {
        Order order = new Order(1L, new ArrayList<>(), new ArrayList<>());

        Order saved = orderRepository.save(order);

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




