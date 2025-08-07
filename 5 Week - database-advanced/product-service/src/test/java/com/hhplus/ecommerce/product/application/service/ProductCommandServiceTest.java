package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.application.port.out.ProductCommandRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductQueryRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCommandServiceTest {

    @Mock
    private ProductCommandRepository productCommandRepository;

    @Mock
    private ProductQueryRepository productQueryRepository;

    @InjectMocks
    private ProductCommandService productCommandService;

    @Test
    void stock_deduct_success() {
        Product product = new Product(1L, "테스트 상품", new BigDecimal("10000"), 10, 0, 0L);
        product.setReservedStock(5);
        when(productQueryRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productCommandRepository.save(any())).thenReturn(product);

        productCommandService.deductStock(1L, 3);

        assertEquals(7, product.getStock());
        assertEquals(2, product.getReservedStock());
        verify(productCommandRepository).save(product);
    }

}










