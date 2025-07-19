package kr.hhplus.be.server.balance;

import kr.hhplus.be.server.balance.dto.BalanceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    public BalanceResponse getMockBalanceByUserId(Long userId) {
        return new BalanceResponse(userId, new BigDecimal("10000"));
    }

}
