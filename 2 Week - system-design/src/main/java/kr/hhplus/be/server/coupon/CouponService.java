package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.dto.CouponResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    public List<CouponResponse> getAllMockCoupons() {
        return List.of(
                new CouponResponse(1L, "신규회원 할인", "WELCOME10", "PERCENTAGE",
                        new BigDecimal("10"), new BigDecimal("0"),
                        1000, 100, LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().plusDays(30), true),
                new CouponResponse(2L, "5000원 할인", "SAVE5000", "AMOUNT",
                        new BigDecimal("5000"), new BigDecimal("50000"),
                        500, 50, LocalDateTime.now().minusDays(10),
                        LocalDateTime.now().plusDays(10), true),
                new CouponResponse(3L, "VIP 20% 할인", "VIP20", "PERCENTAGE",
                        new BigDecimal("20"), new BigDecimal("100000"),
                        100, 20, LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().plusDays(60), true)
        );
    }

    public List<CouponResponse> getMockAvailableCoupons() {
        return List.of(
                new CouponResponse(1L, "신규회원 할인", "WELCOME10", "PERCENTAGE",
                        new BigDecimal("10"), new BigDecimal("0"),
                        1000, 100, LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().plusDays(30), true),
                new CouponResponse(2L, "5000원 할인", "SAVE5000", "AMOUNT",
                        new BigDecimal("5000"), new BigDecimal("50000"),
                        500, 50, LocalDateTime.now().minusDays(10),
                        LocalDateTime.now().plusDays(10), true)
        );
    }

    public CouponResponse getMockCouponById(Long couponId) {
        return new CouponResponse(couponId, "신규회원 할인", "WELCOME10", "PERCENTAGE",
                new BigDecimal("10"), new BigDecimal("0"),
                1000, 100, LocalDateTime.now().minusDays(30),
                LocalDateTime.now().plusDays(30), true);
    }
}
