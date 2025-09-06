package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.domain.Balance;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(BalanceRepositoryAdapter.class)
@ActiveProfiles("test-no-kafka")
public class BalanceRepositoryAdapterTest {

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
        registry.add("app.redis.enabled", () -> "false");
    }

    @Autowired
    private BalanceRepositoryAdapter repository;

    @Test
    void save_and_find() {
        Balance balance = new Balance(1L, new BigDecimal("1000"));
        repository.save(balance);

        Optional<Balance> found = repository.findByUserId(1L);
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("1000"), found.get().getAmount());
    }

    @Test
    void find_not_found() {
        Optional<Balance> found = repository.findByUserId(2L);
        assertFalse(found.isPresent());
    }

}









