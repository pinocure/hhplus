package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.adapter.out.persistence.query.ProductQueryAdapter;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductQueryServiceTest {

    @Mock
    private ProductQueryRepository productQueryRepository;

    @InjectMocks
    private ProductQueryService productQueryService;

    @Test
    void product_search_success() {
        Product product = new Product(1L, "테스트 상품", new BigDecimal("10000"), 10, 0, 0L);
        when(productQueryRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productQueryService.getProduct(1L);

        assertNotNull(result);
        assertEquals("테스트 상품", result.getName());
        verify(productQueryRepository).findById(1L);
    }

}









