package com.hhplus.ecommerce.coupon;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.application.service.CouponService;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CouponServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("coupon")
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
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("쿠폰 발급 성공 테스트 - Redis 없이 DB 로직 사용")
    @Transactional
    void issueCoupon_Success_WithDatabaseLogic() {
        // Given
        CouponEvent event = new CouponEvent(1L, "테스트 이벤트", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        Long userId = 1L;
        Long eventId = 1L;

        CouponService couponService = (CouponService) couponUseCase;
        ReflectionTestUtils.setField(couponService, "redisTemplate", null);

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
    }



}








