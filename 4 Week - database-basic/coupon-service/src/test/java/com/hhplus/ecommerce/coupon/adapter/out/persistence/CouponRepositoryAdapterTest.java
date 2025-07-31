package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    }

    @Autowired
    private CouponRepositoryAdapter repository;

    @Test
    void save_and_find_coupon() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        repository.save(coupon);

        Optional<Coupon> found = repository.findByCode("CODE1");
        assertTrue(found.isPresent());
        assertEquals("CODE1", found.get().getCode());
    }

    @Test
    void find_coupon_not_found() {
        Optional<Coupon> found = repository.findByCode("CODE2");
        assertFalse(found.isPresent());
    }

    @Test
    void save_and_find_event() {
        CouponEvent event = new CouponEvent(2L, "Event2", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        repository.saveEvent(event);

        Optional<CouponEvent> found = repository.findEventById(2L);
        assertTrue(found.isPresent());
        assertEquals("Event2", found.get().getName());
    }

    @Test
    void find_by_user_and_event() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        repository.save(coupon);

        Optional<Coupon> found = repository.findByUserIdAndEventId(1L, 1L);
        assertTrue(found.isPresent());
        assertEquals("CODE1", found.get().getCode());
    }

    @Test
    void checkEventVersion_success() {
        CouponEvent event = new CouponEvent(3L, "Event3", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        repository.saveEvent(event);

        boolean result = repository.checkEventVersion(3L, 0L);

        assertTrue(result);
    }

    @Test
    void checkEventVersion_conflict() {
        CouponEvent event = new CouponEvent(3L, "Event4", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        repository.saveEvent(event);

        boolean result = repository.checkEventVersion(4L, 987L);

        assertFalse(result);
    }

    @Test
    void find_default_event() {
        CouponEvent defaultEvent = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        repository.saveEvent(defaultEvent);

        Optional<CouponEvent> found = repository.findEventById(1L);

        assertTrue(found.isPresent());
        assertEquals("Event1", found.get().getName());
    }

}









