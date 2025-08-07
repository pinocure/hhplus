package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.BalanceFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BalanceFeignAdapterTest {

    @Mock
    private BalanceFeignClient balanceFeignClient;

    @InjectMocks
    private BalanceFeignAdapter balanceFeignAdapter;

    @Test
    void balance_deduct_success() {
        doNothing().when(balanceFeignClient).deductBalance(1L, new BigDecimal("10000"));

        assertDoesNotThrow(() -> balanceFeignAdapter.deductBalance(1L, new BigDecimal("10000")));
        verify(balanceFeignClient).deductBalance(1L, new BigDecimal("10000"));
    }

}










