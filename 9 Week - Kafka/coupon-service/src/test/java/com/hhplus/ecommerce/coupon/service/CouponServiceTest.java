package com.hhplus.ecommerce.coupon.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.application.service.CouponService;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponService(couponRepository);
        ReflectionTestUtils.setField(couponService, "redisTemplate", null);
    }

    @Test
    void issueCoupon_success() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        event.setVersion(0L);

        when(couponRepository.findEventById(1L)).thenReturn(Optional.of(event));
        when(couponRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(couponRepository.saveEvent(any(CouponEvent.class))).thenAnswer(invocation -> {
            CouponEvent savedEvent = invocation.getArgument(0);
            savedEvent.setVersion(savedEvent.getVersion() != null ? savedEvent.getVersion() + 1 : 1L);
            return savedEvent;
        });

        Coupon result = couponService.issueCoupon(1L, 1L);

        assertNotNull(result.getCode());
        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("500"), result.getDiscountAmount());
        assertEquals(9, event.getRemainingQuantity());
    }

    @Test
    void issueCoupon_alreadyIssued() {
        CouponEvent event = new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7));
        Coupon existingCoupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(7));

        when(couponRepository.findEventById(1L)).thenReturn(Optional.of(event));
        when(couponRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.of(existingCoupon));

        assertThrows(BusinessException.class, () -> couponService.issueCoupon(1L, 1L));
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

        assertThrows(BusinessException.class, () -> couponService.validateCoupon("CODE1"));
    }



}












