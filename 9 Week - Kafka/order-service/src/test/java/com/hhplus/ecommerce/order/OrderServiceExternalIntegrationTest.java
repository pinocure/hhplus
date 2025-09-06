package com.hhplus.ecommerce.order;

import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.service.OrderProcessingStatus;
import com.hhplus.ecommerce.order.application.service.OrderService;
import com.hhplus.ecommerce.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.redis.redisson.config=",
        "spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2"
})
@Testcontainers
@Transactional
@DirtiesContext
public class OrderServiceExternalIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("orders")
            .withUsername("ruang")
            .withPassword("ruang");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private OrderService orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ConcurrentMap<Long, OrderProcessingStatus> processingStatus;

    @BeforeEach
    void setUp() {
        processingStatus = (ConcurrentMap<Long, OrderProcessingStatus>)
                ReflectionTestUtils.getField(orderService, "processingStatus");
        if (processingStatus != null) {
            processingStatus.clear();
        }
    }

    @Test
    @DisplayName("주문 생성 및 결제 완료 - Kafka 이벤트 기반")
    public void completeOrderFlow() {
        Long userId = 1L;
        Long productId = 1L;
        Integer quantity = 2;

        Order createdOrder = orderUseCase.createOrder(
                userId,
                List.of(productId),
                List.of(quantity),
                List.of()
        );

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("CONFIRMED", createdOrder.getStatus());
        assertEquals(userId, createdOrder.getUserId());

        Order processingOrder = orderUseCase.payOrder(createdOrder.getId());


        assertEquals("PROCESSING", processingOrder.getStatus());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(processingStatus.containsKey(createdOrder.getId()));
        });

        System.out.println("주문 생성 및 결제 요청이 완료되었습니다.");
    }

}








