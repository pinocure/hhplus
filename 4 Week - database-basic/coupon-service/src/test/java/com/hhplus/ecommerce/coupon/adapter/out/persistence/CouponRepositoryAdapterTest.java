package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CouponRepositoryAdapterTest {

    private final CouponRepository repository = new CouponRepositoryAdapter();

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

}









