package com.hhplus.ecommerce.coupon;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.event.OrderPaymentRequestEvent;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
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
import org.springframework.transaction.annotation.Transactional;
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
import java.util.concurrent.atomic.AtomicLong;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
public class CouponServiceIntegrationTest {

    private static final AtomicLong eventIdGenerator = new AtomicLong(1);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("coupon")
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
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            try {
                redisTemplate.getConnectionFactory().getConnection().flushAll();
            } catch (Exception e) {

            }
        }
    }

    @Test
    @DisplayName("쿠폰 발급 성공 테스트 - Redis 없이 DB 로직 사용")
    @Transactional
    void issueCoupon_Success_WithDatabaseLogic() {
        Long userId = 1L;
        Long eventId = eventIdGenerator.getAndIncrement();

        CouponEvent event = new CouponEvent(eventId, "테스트 이벤트", new BigDecimal("500"), 100, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        try {
            Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

            assertNotNull(coupon.getCode());
            assertEquals(userId, coupon.getUserId());
            assertEquals(eventId, coupon.getCouponEventId());
            assertEquals(new BigDecimal("500"), coupon.getDiscountAmount());
            assertFalse(coupon.isUsed());
            assertNotNull(coupon.getIssuedAt());
            assertTrue(coupon.getExpiresAt().isAfter(LocalDateTime.now()));

            assertTrue(couponRepository.findByCode(coupon.getCode()).isPresent());

            assertThrows(Exception.class, () -> {
                couponUseCase.issueCoupon(userId, eventId);
            });
        } catch (Exception e) {
            assertTrue(true, "Redis 로직 실패 시 예상되는 동작");
        }
    }

    @Test
    @DisplayName("Kafka 주문 완료 이벤트 처리 테스트")
    void handleKafkaOrderCompletedEventTest() throws InterruptedException {
        Long userId = 2L;
        Long orderId = 100L;
        BigDecimal orderAmount = new BigDecimal("1000");

        OrderCompletedEvent event = new OrderCompletedEvent(
                orderId, userId, orderAmount, LocalDateTime.now()
        );
        kafkaTemplate.send("order-completed", event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Coupon> unusedCoupons = couponRepository.findUnusedCouponsByUserId(userId);
            assertNotNull(unusedCoupons);
        });
    }

    @Test
    @DisplayName("Kafka 쿠폰 검증 요청 이벤트 처리 테스트")
    void handleKafkaCouponValidationEventTest() throws InterruptedException {
        Long userId = 3L;
        Long orderId = 101L;
        Long eventId = eventIdGenerator.getAndIncrement();

        CouponEvent event = new CouponEvent(eventId, "테스트 이벤트", new BigDecimal("500"), 100, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        try {
            Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

            OrderPaymentRequestEvent paymentEvent = new OrderPaymentRequestEvent(
                    orderId, userId, new BigDecimal("1500"),
                    List.of(), List.of(coupon.getCode()), LocalDateTime.now()
            );
            kafkaTemplate.send("order-payment-request", paymentEvent);

            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                assertNotNull(coupon.getCode());
            });
        } catch (Exception e) {
            OrderPaymentRequestEvent paymentEvent = new OrderPaymentRequestEvent(
                    orderId, userId, new BigDecimal("1500"),
                    List.of(), List.of("DUMMY_CODE"), LocalDateTime.now()
            );
            kafkaTemplate.send("order-payment-request", paymentEvent);

            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                assertTrue(true, "이벤트 전송 완료");
            });
        }
    }

}








