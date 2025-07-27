package com.hhplus.ecommerce.order.application.port.out;

import java.math.BigDecimal;

public interface BalancePort {

    BigDecimal getBalance(Long userId);
    void deductBalance(Long userId, BigDecimal amount);

}
