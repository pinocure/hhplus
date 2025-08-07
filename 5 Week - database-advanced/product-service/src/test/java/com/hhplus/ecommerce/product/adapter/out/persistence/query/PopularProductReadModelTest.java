package com.hhplus.ecommerce.product.adapter.out.persistence.query;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PopularProductReadModelTest {

    @Test
    void increaseSalesCount_success() {
        PopularProductReadModel readModel = new PopularProductReadModel(
                1L,
                "테스트 상품",
                new BigDecimal("10000"),
                100,
                10,
                LocalDateTime.now()
        );

        readModel.increaseSalesCount(5);

        assertEquals(15, readModel.getSalesCount());
        assertNotNull(readModel.getLastUpdated());
    }

}











