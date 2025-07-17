package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public List<CouponResponse> getAllMockCoupons() {
        return couponService.getAllMockCoupons();
    }

    @GetMapping("/available")
    public List<CouponResponse> getAvailableCoupons() {
        return couponService.getMockAvailableCoupons();
    }

    @GetMapping("/{couponId}")
    public CouponResponse getCouponById(@PathVariable long couponId) {
        return couponService.getMockCouponById(couponId);
    }

}
