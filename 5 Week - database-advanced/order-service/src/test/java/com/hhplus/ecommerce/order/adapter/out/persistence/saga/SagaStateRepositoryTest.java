package com.hhplus.ecommerce.order.adapter.out.persistence.saga;

import com.hhplus.ecommerce.order.domain.saga.SagaTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
public class SagaStateRepositoryTest {

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
    }

    @Autowired
    private SagaStateRepository repository;


    @Test
    void saveAndFindSagaTransaction() {
        // given
        SagaTransaction saga = new SagaTransaction("SAGA-123", 1L);
        saga.addStep("STOCK_RESERVED");
        saga.complete();

        // when
        repository.save(saga);
        Optional<SagaTransaction> found = repository.findSagaById("SAGA-123");

        // then
        assertTrue(found.isPresent());
        assertEquals("COMPLETED", found.get().getStatus());
        assertEquals("STOCK_RESERVED", found.get().getCurrentStep());
    }

}






