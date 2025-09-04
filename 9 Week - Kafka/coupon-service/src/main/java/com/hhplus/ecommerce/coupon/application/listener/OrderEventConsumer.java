package com.hhplus.ecommerce.coupon.application.listener;

import com.hhplus.ecommerce.common.event.CouponInfo;
import com.hhplus.ecommerce.common.event.CouponValidationCompletedEvent;
import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.event.OrderPaymentRequestEvent;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final CouponUseCase couponUseCase;
    private final CouponRepository couponRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-payment-request", groupId = "coupon-service-group")
    @Transactional
    public void handleCouponValidation(OrderPaymentRequestEvent event) {
        try {
            List<CouponInfo> validCoupons = new ArrayList<>();
            BigDecimal totalDiscount = BigDecimal.ZERO;

            if (event.getCouponCodes().isEmpty()) {
                CouponValidationCompletedEvent successEvent = new CouponValidationCompletedEvent(
                        event.getOrderId(),
                        event.getUserId(),
                        true,
                        null,
                        validCoupons,
                        BigDecimal.ZERO,
                        LocalDateTime.now()
                );
                kafkaTemplate.send("coupon-validation-completed", successEvent);
                return;
            }

            for (String couponCode : event.getCouponCodes()) {
                try {
                    couponUseCase.validateCoupon(couponCode);

                    BigDecimal discountAmount = couponUseCase.getCouponDiscountAmount(couponCode);

                    validCoupons.add(new CouponInfo(couponCode, discountAmount));
                    totalDiscount = totalDiscount.add(discountAmount);
                } catch (Exception e) {
                    CouponValidationCompletedEvent failedEvent = new CouponValidationCompletedEvent(
                            event.getOrderId(),
                            event.getUserId(),
                            false,
                            "쿠폰 검증 실패: " + couponCode + " - " + e.getMessage(),
                            new ArrayList<>(),
                            BigDecimal.ZERO,
                            LocalDateTime.now()
                    );

                    kafkaTemplate.send("coupon-validation-completed", failedEvent);
                    return;
                }
            }

            CouponValidationCompletedEvent successEvent = new CouponValidationCompletedEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    true,
                    null,
                    validCoupons,
                    totalDiscount,
                    LocalDateTime.now()
            );

            kafkaTemplate.send("coupon-validation-completed", successEvent);
        } catch (Exception e) {
            log.error("쿠폰 처리 실패: orderId={}, error={}", event.getOrderId(), e.getMessage());
        }
    }


    @KafkaListener(topics = "order-completed", groupId = "coupon-usage-group")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            List<Coupon> unusedCoupons = couponRepository.findUnusedCouponsByUserId(event.getUserId());

            for (Coupon coupon : unusedCoupons) {
                if (!coupon.isUsed()) {
                    coupon.use(); // 쿠폰 사용 처리
                    couponRepository.save(coupon);
                    log.info("쿠폰 사용 처리: couponCode={}", coupon.getCode());
                }
            }
        } catch (Exception e) {
            log.error("쿠폰 사용 처리 실패: {}", e.getMessage());
        }
    }

}











