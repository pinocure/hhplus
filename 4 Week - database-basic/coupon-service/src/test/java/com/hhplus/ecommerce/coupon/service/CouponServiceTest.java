package com.hhplus.ecommerce.coupon.service;

import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.application.service.CouponService;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        couponService = new CouponService(couponRepository);
    }

    @Test
    void issueCoupon_success() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        when(couponRepository.findEventById(1L)).thenReturn(Optional.of(event));
        when(couponRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(couponRepository.checkEventVersion(1L, 0L)).thenReturn(true);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(couponRepository.saveEvent(any(CouponEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coupon result = couponService.issueCoupon(1L, 1L);

        assertNotNull(result.getCode());
        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("500"), result.getDiscountAmount());
        assertEquals(1L, event.getVersion());
    }

    @Test
    void issueCoupon_alreadyIssued() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        Coupon existingCoupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(7));
        when(couponRepository.findEventById(1L)).thenReturn(Optional.of(event));
        when(couponRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.of(existingCoupon));

        assertThrows(IllegalStateException.class, () -> couponService.issueCoupon(1L, 1L));
    }

    @Test
    void validateCoupon_success() {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        when(couponRepository.findByCode("CODE1")).thenReturn(Optional.of(coupon));

        assertDoesNotThrow(() -> couponService.validateCoupon("CODE1"));
    }

    @Test
    void validateCoupon_notFound() {
        when(couponRepository.findByCode("CODE1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponService.validateCoupon("CODE1"));
    }

    @Test
    void issueCoupon_concurrencyConflict() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        when(couponRepository.findEventById(1L)).thenReturn(Optional.of(event));
        when(couponRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(couponRepository.checkEventVersion(1L, 0L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> couponService.issueCoupon(1L, 1L));
    }

}












