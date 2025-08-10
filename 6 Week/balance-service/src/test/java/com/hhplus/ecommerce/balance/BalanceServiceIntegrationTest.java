package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class BalanceServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("balance")
            .withUsername("ruang")
            .withPassword("ruang");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private BalanceUseCase balanceUseCase;

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







