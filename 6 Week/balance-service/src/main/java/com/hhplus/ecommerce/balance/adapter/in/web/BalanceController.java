package com.hhplus.ecommerce.balance.adapter.in.web;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 역할: balance 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: 잔액 관련 HTTP 요청을 처리하고, BalanceUseCase를 호출하여 유즈케이스 흐름을 트리거
 */

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceUseCase balanceUseCase;

    public BalanceController(BalanceUseCase balanceUseCase) {
        this.balanceUseCase = balanceUseCase;
    }

    @PostMapping("/charge")
    public ResponseEntity<BigDecimal> charge(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        BigDecimal newBalance = balanceUseCase.chargeBalance(userId, amount);
        return ResponseEntity.ok(newBalance);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        BigDecimal balance = balanceUseCase.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/deduct")
    public ResponseEntity<Void> deductBalance(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        balanceUseCase.deductBalance(userId, amount);
        return ResponseEntity.ok().build();
    }

}












