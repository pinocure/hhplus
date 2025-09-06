package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
@DirtiesContext
public class BalanceServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("balance_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.hikari.max-lifetime", () -> "30000");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "3000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "5");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("app.redis.enabled", () -> "true");
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private BalanceUseCase balanceUseCase;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {

    }

    @Test
    void chargeBalanceIntegrationTest() {
        Long userId = 1L;
        BigDecimal chargeAmount = new BigDecimal("1000");

        BigDecimal result = balanceUseCase.chargeBalance(userId, chargeAmount);

        assertEquals(0, chargeAmount.compareTo(result));
        assertEquals(0, chargeAmount.compareTo(balanceUseCase.getBalance(userId)));
    }

    @Test
    void deductBalanceIntegrationTest() {
        Long userId = 2L;
        BigDecimal initialAmount = new BigDecimal("10000");
        BigDecimal deductAmount = new BigDecimal("500");
        BigDecimal expectedAmount = new BigDecimal("9500");

        balanceUseCase.chargeBalance(userId, initialAmount);

        balanceUseCase.deductBalance(userId, deductAmount);

        assertEquals(0, new BigDecimal("9500").compareTo(balanceUseCase.getBalance(userId)));
    }

    @Test
    void handleKafkaOrderCompletedEventTest() throws InterruptedException {
        Long userId = 3L;
        Long orderId = 100L;
        BigDecimal orderAmount = new BigDecimal("1000");
        BigDecimal initialAmount = new BigDecimal("10000");

        balanceUseCase.chargeBalance(userId, initialAmount);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        OrderCompletedEvent event = new OrderCompletedEvent(
                orderId, userId, orderAmount, LocalDateTime.now()
        );
        kafkaTemplate.send("order-completed", event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BigDecimal currentBalance = balanceUseCase.getBalance(userId);
            assertEquals(0, initialAmount.compareTo(currentBalance),
                    "Order completed 이벤트는 잔액을 변경하지 않고 단순히 기록만");
        });
    }

}







