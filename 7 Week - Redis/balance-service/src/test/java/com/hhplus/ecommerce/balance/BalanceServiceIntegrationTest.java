package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
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
    }

    @Autowired
    private BalanceUseCase balanceUseCase;

    @Autowired
    private BalanceRepository balanceRepository;

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
        balanceUseCase.chargeBalance(userId, new BigDecimal("1000"));

        BigDecimal deductAmount = new BigDecimal("500");
        balanceUseCase.deductBalance(userId, deductAmount);

        assertEquals(0, new BigDecimal("500").compareTo(balanceUseCase.getBalance(userId)));
    }

}







