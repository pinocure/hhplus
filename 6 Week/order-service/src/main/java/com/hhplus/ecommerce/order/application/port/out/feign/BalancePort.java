package com.hhplus.ecommerce.order.application.port.out.feign;

import java.math.BigDecimal;

public interface BalancePort {

    BigDecimal getBalance(Long userId);
    void deductBalance(Long userId, BigDecimal amount);

}
