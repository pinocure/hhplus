package com.hhplus.ecommerce.order.adapter.out.external.adapter;

import com.hhplus.ecommerce.order.adapter.out.external.client.ProductFeignClient;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductFeignAdapterTest {

    @Mock
    private ProductFeignClient productFeignClient;

    @InjectMocks
    private ProductFeignAdapter productFeignAdapter;

    @Test
    void product_search_success() {
        ProductFeignClient.ProductDto feignDto = new ProductFeignClient.ProductDto();
        feignDto.setId(1L);
        feignDto.setName("테스트 상품");
        feignDto.setPrice(new BigDecimal("10000"));
        feignDto.setStock(10);

        when(productFeignClient.getProduct(1L)).thenReturn(feignDto);

        ProductPort.ProductDto result = productFeignAdapter.getProduct(1L);

        assertNotNull(result);
        assertEquals("테스트 상품", result.getName());
        verify(productFeignClient).getProduct(1L);
    }

}











