package com.hhplus.ecommerce.product;

import com.hhplus.ecommerce.product.adapter.out.persistence.ProductRepositoryAdapter;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import({ProductRepositoryAdapter.class})
public class ProductRankingTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("app.redis.enabled", () -> "true");
    }

    @Autowired
    private ProductUseCase productUseCase;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
        }

        for (int i=1; i<=3; i++) {
            Product product = new Product(
                    null,
                    "테스트상품" + i,
                    BigDecimal.valueOf(10000 * i),
                    100,
                    0,
                    0L
            );
            productRepository.save(product);
        }
    }


    @Test
    @DisplayName("상품 주문 시 랭킹이 업데이트되고 getPopularProducts에 반영")
    void testProductRankingWithDeductStock() {
        List<Product> allProducts = productRepository.findAll();

        Product product1 = allProducts.get(0);
        Product product2 = allProducts.get(1);
        Product product3 = allProducts.get(2);



        for (int i = 0; i < 5; i++) {
            productUseCase.deductStock(product2.getId(), 1);
        }

        for (int i = 0; i < 3; i++) {
            productUseCase.deductStock(product1.getId(), 1);
        }

        productUseCase.deductStock(product3.getId(), 1);



        List<Product> popularProducts = productUseCase.getPopularProducts(1, 3);

        assertFalse(popularProducts.isEmpty(), "인기 상품 목록이 비어있으면 안됨");


        assertEquals(product2.getId(), popularProducts.get(0).getId(),
                "가장 많이 주문된 상품이 1위여야 함");

        if (popularProducts.size() > 1) {
            assertEquals(product1.getId(), popularProducts.get(1).getId(),
                    "두 번째로 많이 주문된 상품이 2위여야 함");
        }

        if (popularProducts.size() > 2) {
            assertEquals(product3.getId(), popularProducts.get(2).getId(),
                    "세 번째로 많이 주문된 상품이 3위여야 함");
        }
    }

    @Test
    @DisplayName("재고 차감과 복구가 정상 동작")
    void testStockOperations() {
        Product product = productRepository.findAll().get(0);
        Long productId = product.getId();
        Integer initialStock = product.getStock();

        productUseCase.deductStock(productId, 5);

        Product updated = productRepository.findById(productId).orElseThrow();
        assertEquals(initialStock - 5, updated.getStock());

        productUseCase.restoreStock(productId, 3);

        Product restored = productRepository.findById(productId).orElseThrow();
        assertEquals(initialStock - 2, restored.getStock());
    }

}









