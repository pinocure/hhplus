package com.hhplus.ecommerce.order.adapter.out.external;


import com.hhplus.ecommerce.order.application.port.out.BalancePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 역할: Balance 서비스와의 통신을 담당하는 외부 어댑터
 * 책임: HTTP 통신을 통해 Balance 서비스의 기능을 호출
 */

@Component
public class BalanceClientAdapter implements BalancePort {

    private final RestTemplate restTemplate;
    private final String balanceServiceUrl;

    public BalanceClientAdapter(RestTemplate restTemplate,
                                @Value("${services.balance.url}") String balanceServiceUrl) {
        this.restTemplate = restTemplate;
        this.balanceServiceUrl = balanceServiceUrl;
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        try {
            return restTemplate.getForObject(balanceServiceUrl + "/balances/" + userId, BigDecimal.class);
        } catch (Exception e) {
            throw new RuntimeException("잔액 조회 실패 : " + e.getMessage());
        }
    }

    @Override
    public void deductBalance(Long userId, BigDecimal amount) {
        try {
            restTemplate.postForObject(balanceServiceUrl + "/balances/deduct?userId=" + userId + "&amount=" + amount, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("잔액 차감 실패 : " + e.getMessage());
        }
    }

}











