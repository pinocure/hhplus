package com.hhplus.ecommerce.product.domain.event;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Value
public class ProductSoldEvent {
    Long productId;
    String name;
    BigDecimal price;
    Integer stock;
    int quantity;
    LocalDateTime occurredAt;
}




