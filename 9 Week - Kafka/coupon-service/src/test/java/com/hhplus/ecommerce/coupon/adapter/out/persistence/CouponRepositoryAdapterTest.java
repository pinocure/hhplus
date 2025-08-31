package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(CouponRepositoryAdapter.class)
@ActiveProfiles("test-no-redis")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CouponRepositoryAdapterTest {

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
        registry.add("app.redis.enabled", () -> "false");
    }

    @Autowired
    private CouponRepositoryAdapter repository;

    @Test
    @Transactional
    void save_and_find_coupon() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));

        repository.save(coupon);
        Optional<Coupon> found = repository.findByCode("CODE1");

        assertTrue(found.isPresent());
        assertEquals("CODE1", found.get().getCode());
    }

    @Test
    @Transactional
    void find_coupon_not_found() {
        Optional<Coupon> found = repository.findByCode("NO EXIST");
        assertFalse(found.isPresent());
    }

    @Test
    @Transactional
    void save_and_find_event() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));

        CouponEvent saved = repository.saveEvent(event);

        assertNotNull(saved);
        assertEquals(0L, saved.getVersion());

        Optional<CouponEvent> found = repository.findEventById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Event1", found.get().getName());
    }

    @Test
    @Transactional
    void find_by_user_and_event() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        repository.saveEvent(event);

        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        repository.save(coupon);

        Optional<Coupon> found = repository.findByUserIdAndEventId(1L, 1L);

        assertTrue(found.isPresent());
        assertEquals("CODE1", found.get().getCode());
    }

    @Test
    void find_default_event() {
        CouponEvent defaultEvent = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));

        CouponEvent saved = repository.saveEvent(defaultEvent);
        Optional<CouponEvent> found = repository.findEventById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Event1", found.get().getName());
    }

}









