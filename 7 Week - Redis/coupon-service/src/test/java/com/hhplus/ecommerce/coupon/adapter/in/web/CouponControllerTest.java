package com.hhplus.ecommerce.coupon.adapter.in.web;

import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CouponControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponUseCase couponUseCase;

    @InjectMocks
    private CouponController couponController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
    }

    @Test
    void issueCoupon_success() throws Exception {
        Coupon coupon = new Coupon("CODE1", 1L, 1L, new BigDecimal("500"), LocalDateTime.now().plusDays(1));
        when(couponUseCase.issueCoupon(1L, 1L)).thenReturn(coupon);

        mockMvc.perform(post("/coupons/issue")
                        .param("userId", "1")
                        .param("eventId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void validateCoupon_success() throws Exception {
        doNothing().when(couponUseCase).validateCoupon("CODE1");

        mockMvc.perform(post("/coupons/validate")
                        .param("couponCode", "CODE1"))
                .andExpect(status().isOk());
    }

}










