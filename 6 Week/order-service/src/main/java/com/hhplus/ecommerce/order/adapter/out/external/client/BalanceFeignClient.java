package com.hhplus.ecommerce.order.adapter.out.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;


@FeignClient(name = "balance-service", url = "${services.balance.url}")
public interface BalanceFeignClient {

    @GetMapping("/balances/{userId}")
    BigDecimal getBalance(@PathVariable("userId") Long userId);

    @PostMapping("/balances/deduct")
    void deductBalance(@RequestParam("userId") Long userId,
                       @RequestParam("amount") BigDecimal amount);
}
