package com.hhplus.ecommerce.coupon;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class CouponServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("coupon")
            .withUsername("ruang")
            .withPassword("ruang");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        // 테스트용 기본 이벤트 생성
        if (couponRepository.findEventById(1L).isEmpty()) {
            CouponEvent defaultEvent = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
            couponRepository.saveEvent(defaultEvent);
        }
    }


    @Test
    void issueCouponIntegrationTest() {
        Long userId = 1L;
        Long eventId = 1L;

        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

        assertNotNull(coupon.getCode());
        assertEquals(userId, coupon.getUserId());
    }

    @Test
    void validateCouponIntegrationTest() {
        Long userId = 2L;
        Long eventId = 1L;
        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

        // 예외 발생 X, 쿠폰 검증 성공
        assertDoesNotThrow(() -> couponUseCase.validateCoupon(coupon.getCode()));
    }

    @Test
    void getCouponDiscountAmountIntegrationTest() {
        Long userId = 3L;
        Long eventId = 1L;
        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

        BigDecimal discountAmount = couponUseCase.getCouponDiscountAmount(coupon.getCode());

        assertEquals(0, new BigDecimal("500").compareTo(discountAmount));
    }

}








