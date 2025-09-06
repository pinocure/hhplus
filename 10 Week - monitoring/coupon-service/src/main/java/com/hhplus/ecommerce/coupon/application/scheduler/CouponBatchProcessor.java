package com.hhplus.ecommerce.coupon.application.scheduler;

import com.hhplus.ecommerce.coupon.application.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RedisTemplate.class)
public class CouponBatchProcessor {

    private final CouponService couponService;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 5000)
    public void processCouponQueues() {
        if (redisTemplate == null) return;

        try {
            Set<String> queueKeys = redisTemplate.keys("coupon:queue:*");
            if (queueKeys != null) {
                for (String queueKey : queueKeys) {
                    String[] parts = queueKey.split(":");
                    if (parts.length >= 3) {
                        Long eventId = Long.parseLong(parts[2]);
                        couponService.processCouponQueue(eventId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("쿠폰 배치 처리 실패", e);
        }
    }

    @Scheduled(fixedDelay = 3600000)
    public void retryFailedCoupons() {
        if (redisTemplate == null) return;

        try {
            Set<String> failedKeys = redisTemplate.keys("coupon:failed:*");
            if (failedKeys != null) {
                for (String failedKey : failedKeys) {
                    String[] parts = failedKey.split(":");
                    if (parts.length >= 3) {
                        Long eventId = Long.parseLong(parts[2]);
                        String queueKey = "coupon:queue:" + eventId;

                        String userId;
                        while ((userId = redisTemplate.opsForList().leftPop(queueKey)) != null) {
                            redisTemplate.opsForList().rightPush(queueKey, userId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("실패 큐 재처리 실패", e);
        }
    }

}








