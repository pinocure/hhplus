package com.hhplus.ecommerce.product;

import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class ProductServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("product")
            .withUsername("ruang")
            .withPassword("ruang");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private ProductUseCase productUseCase;

    @Test
    void getAllProductsIntegrationTest() {
        // 전체 상품 조회하고 상품 없으면 예외 발생
        assertThrows(IllegalStateException.class, () -> {
            productUseCase.getAllProducts();
        });
    }

    @Test
    void getPopularProductsIntegrationTest() {
        // 인기상품 조회
        List<Product> popular = productUseCase.getPopularProducts(3, 5);

        // 빈 리스트 반환
        assertTrue(popular.isEmpty());
    }

}







