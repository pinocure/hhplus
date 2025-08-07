package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.CouponFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponFeignAdapterTest {

    @Mock
    private CouponFeignClient couponFeignClient;

    @InjectMocks
    private CouponFeignAdapter couponFeignAdapter;

    @Test
    void coupon_discount_search_success() {
        when(couponFeignClient.getCouponDiscountAmount("COUPON123")).thenReturn(new BigDecimal("5000"));

        BigDecimal result = couponFeignAdapter.getCouponDiscountAmount("COUPON123");

        assertEquals(new BigDecimal("5000"), result);
        verify(couponFeignClient).getCouponDiscountAmount("COUPON123");
    }

}









