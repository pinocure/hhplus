package com.hhplus.ecommerce.order.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderProduct {

    private Long id;
    private Long productId;
    private String name;
    private BigDecimal price;

    public OrderProduct() {}

    public OrderProduct(Long productId, String name, BigDecimal price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

}







