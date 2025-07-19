package kr.hhplus.be.server.balance;

import kr.hhplus.be.server.balance.dto.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{userId}")
    public BalanceResponse getMockBalanceByUserId(@PathVariable Long userId) {
        return balanceService.getMockBalanceByUserId(userId);
    }

}
