package com.hhplus.ecommerce.coupon;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)        // 테스트간 격리
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


    @Test
    @DisplayName("쿠폰 발급 성공 테스트")
    @Transactional
    void issueCoupon_Success() {
        // Given
        CouponEvent event = new CouponEvent(1L, "테스트 이벤트", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        Long userId = 1L;
        Long eventId = 1L;

        // When
        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

        // Then
        assertNotNull(coupon.getCode());
        assertEquals(userId, coupon.getUserId());
        assertEquals(eventId, coupon.getCouponEventId());
        assertEquals(new BigDecimal("500"), coupon.getDiscountAmount());
        assertFalse(coupon.isUsed());
        assertNotNull(coupon.getIssuedAt());
        assertTrue(coupon.getExpiresAt().isAfter(LocalDateTime.now()));

        // DB에서 실제로 저장되었는지 확인
        assertTrue(couponRepository.findByCode(coupon.getCode()).isPresent());

        // 이벤트 수량이 감소했는지 확인
        CouponEvent updatedEvent = couponRepository.findEventById(eventId).get();
        assertEquals(9, updatedEvent.getRemainingQuantity());
    }

    @Test
    @DisplayName("동시성 테스트 - 여러 사용자가 동시에 쿠폰 발급")
    void issueCoupon_ConcurrentUsers_Success() throws InterruptedException {
        // Given
        CouponEvent event = new CouponEvent(2L, "동시성 테스트 이벤트", new BigDecimal("1000"), 5, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        Long eventId = 2L;
        int userCount = 10; // 5개 쿠폰에 10명이 시도
        ExecutorService executor = Executors.newFixedThreadPool(userCount);
        CountDownLatch startLatch = new CountDownLatch(1); // 동시 시작을 위한 latch
        CountDownLatch endLatch = new CountDownLatch(userCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < userCount; i++) {
            final Long userId = (long) (100 + i);
            executor.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 준비될 때까지 대기
                    Coupon coupon = couponUseCase.issueCoupon(userId, eventId);
                    successCount.incrementAndGet();
                    System.out.println("Success - User: " + userId + ", Coupon: " + coupon.getCode());
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("Failed - User: " + userId + ", Reason: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 모든 스레드 동시 시작
        startLatch.countDown();

        // 모든 스레드 완료 대기
        endLatch.await();
        executor.shutdown();

        // Then
        assertEquals(5, successCount.get(), "정확히 5명만 쿠폰을 받아야 함");
        assertEquals(5, failCount.get(), "나머지 5명은 실패해야 함");

        // 이벤트 수량 확인
        CouponEvent updatedEvent = couponRepository.findEventById(eventId).get();
        assertEquals(0, updatedEvent.getRemainingQuantity(), "쿠폰이 모두 소진되어야 함");
    }

    @Test
    @DisplayName("쿠폰 할인금액 조회 성공")
    @Transactional
    void getCouponDiscountAmount_Valid_Success() {
        // Given
        CouponEvent event = new CouponEvent(3L, "할인 테스트 이벤트", new BigDecimal("1000"), 10, LocalDateTime.now().plusDays(7));
        couponRepository.saveEvent(event);

        Long userId = 200L;
        Long eventId = 3L;
        Coupon coupon = couponUseCase.issueCoupon(userId, eventId);

        // When
        BigDecimal discountAmount = couponUseCase.getCouponDiscountAmount(coupon.getCode());

        // Then
        assertEquals(new BigDecimal("1000"), discountAmount);
    }

}








