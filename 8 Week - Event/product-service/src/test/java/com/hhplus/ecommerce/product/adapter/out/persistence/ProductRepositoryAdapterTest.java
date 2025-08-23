package com.hhplus.ecommerce.product.adapter.out.persistence;

import com.hhplus.ecommerce.common.config.RedisConfig;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Import({ProductRepositoryAdapter.class, RedisConfig.class})
public class ProductRepositoryAdapterTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("product")
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
    private ProductRepositoryAdapter repository;

    @Test
    void findAll_success() {
        Product product = new Product(null, "Test Product", BigDecimal.valueOf(1000), 10, 0, 0L);
        repository.save(product);

        List<Product> products = repository.findAll();
        assertFalse(products.isEmpty());
    }

    @Test
    void findById_success() {
        Product product = new Product(null, "Test Product", BigDecimal.valueOf(1000), 10, 0, 0L);
        Product saved = repository.save(product);

        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
    }

    @Test
    void findById_not_found() {
        Optional<Product> found = repository.findById(999L);

        assertFalse(found.isPresent());;
    }

    @Test
    void findPopular_success() {
        Product product1 = new Product(null, "Product1", BigDecimal.valueOf(1000), 5, 0, 0L);
        Product product2 = new Product(null, "Product2", BigDecimal.valueOf(2000), 10, 0, 0L);
        repository.save(product1);
        repository.save(product2);

        List<Product> popular = repository.findPopular(3, 5);
        assertFalse(popular.isEmpty());
    }

    @Test
    void save_success() {
        Product newProduct = new Product(null, "New Product", BigDecimal.valueOf(3000), 30, 0, 0L);
        Product saved = repository.save(newProduct);

        assertNotNull(saved.getId());
        assertEquals("New Product", saved.getName());
    }

    @Test
    void checkProductVersion_success() {
        Product product = new Product(null, "Test Product", BigDecimal.valueOf(1000), 10, 0, 0L);
        Product saved = repository.save(product);

        boolean result = repository.checkProductVersion(saved.getId(), 0L);
        assertTrue(result);
    }

    @Test
    void checkProductVersion_conflict() {
        Product product = new Product(null, "Test Product", BigDecimal.valueOf(1000), 10, 0, 0L);
        Product saved = repository.save(product);

        boolean result = repository.checkProductVersion(saved.getId(), 999L);
        assertFalse(result);
    }

}









