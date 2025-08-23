package com.hhplus.ecommerce.order.application.port.out.feign;

import java.math.BigDecimal;

public interface CouponPort {

    void validateCoupon(String couponCode);

    BigDecimal getCouponDiscountAmount(String couponCode);

}
