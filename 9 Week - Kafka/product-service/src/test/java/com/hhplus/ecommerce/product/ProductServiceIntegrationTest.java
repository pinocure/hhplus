package com.hhplus.ecommerce.product;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
public class ProductServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("product")
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
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("app.redis.enabled", () -> "true");
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private ProductUseCase productUseCase;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
        }

        Product testProduct1 = new Product(null, "테스트 상품1", new BigDecimal("10000"), 100, 0, 0L);
        Product testProduct2 = new Product(null, "테스트 상품2", new BigDecimal("20000"), 50, 0, 0L);
        Product testProduct3 = new Product(null, "테스트 상품3", new BigDecimal("15000"), 75, 0, 0L);

        productRepository.save(testProduct1);
        productRepository.save(testProduct2);
        productRepository.save(testProduct3);
    }

    @Test
    @DisplayName("전체 상품 조회 통합 테스트")
    void getAllProductsIntegrationTest() {
        List<Product> products = productUseCase.getAllProducts();

        assertNotNull(products);
        assertTrue(products.size() >= 3);
    }

    @Test
    @DisplayName("인기 상품 조회 통합 테스트")
    void getPopularProductsIntegrationTest() {
        List<Product> popular = productUseCase.getPopularProducts(3, 5);

        assertNotNull(popular);
    }

    @Test
    @DisplayName("Kafka 주문 완료 이벤트 처리 테스트")
    void handleKafkaOrderCompletedEventTest() throws InterruptedException {
        Long userId = 1L;
        Long orderId = 100L;
        BigDecimal orderAmount = new BigDecimal("25000");

        OrderCompletedEvent event = new OrderCompletedEvent(
                orderId, userId, orderAmount, LocalDateTime.now()
        );
        kafkaTemplate.send("order-completed", event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertNotNull(event.getOrderId());
        });
    }

}







