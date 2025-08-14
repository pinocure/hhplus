package com.hhplus.ecommerce.product.domain;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Product {

    private Long id;                    // 사용자 ID
    private String name;                // 사용자 이름
    private BigDecimal price;           // 가격
    private Integer stock;              // 재고
    private Integer reservedStock;      // 예약 재고 (주문 대기)
    private Long version;               // 버전관리 (Lock 관련)

    public Product(Long id, String name, BigDecimal price, Integer stock, Integer reservedStock, Long version) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.reservedStock = 0;
        this.version = 0L;
    }

    // 재고 확인
    public boolean hasEnoughStock(int quantity) {
        return this.stock >= quantity;
    }

    // 재고 예약
    public void reserveStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.reservedStock += quantity;
        this.version++;
    }

    public void deductStock(int quantity) {
        if (this.reservedStock < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_RESERCE_STOCK);
        }
        this.stock -= quantity;
        this.reservedStock -= quantity;
        this.version++;
    }

    public void rollbackReservedStock(int quantity) {
        this.reservedStock -= quantity;
        this.version++;
    }

}












