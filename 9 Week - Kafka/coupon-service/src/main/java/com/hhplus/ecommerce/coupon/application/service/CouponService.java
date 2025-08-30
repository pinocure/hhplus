package com.hhplus.ecommerce.coupon.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.coupon.application.port.in.CouponUseCase;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponService implements CouponUseCase {

    private final CouponRepository couponRepository;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    private static final String ISSUE_COUPON_SCRIPT =
            "local key = KEYS[1]" +
            "local userId = KEYS[2]" +
            "local maxCount = tonumber(ARGV[1])" +
            "local currentCount = redis.call('scard', key)" +
            "if currentCount >= maxCount then" +
            "    return -1" +
            "end" +
            "local isMember = redis.call('sismember', key, userId)" +
            "if isMember == 1 then" +
            "    return 0" +
            "end" +
            "redis.call('sadd', key, userId)" +
            "return 1";


    @Override
    @Transactional
    public Coupon issueCoupon(Long userId, Long eventId) {
        CouponEvent event = couponRepository.findEventById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_SOLD_OUT));

        if (LocalDateTime.now().isAfter(event.getExpiresAt())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        if (redisTemplate != null) {
            String issueResult = executeCouponIssueWithRedis(userId, eventId, event.getTotalQuantity());

            if ("ALREADY_ISSUED".equals(issueResult)) {
                throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
            }

            if ("SOLD_OUT".equals(issueResult)) {
                throw new BusinessException(ErrorCode.COUPON_SOLD_OUT);
            }

            return saveIssuedCoupon(userId, eventId, event);
        } else {
            return issueCouponWithDatabase(userId, eventId, event);
        }

    }

    private String executeCouponIssueWithRedis(Long userId, Long eventId, Integer maxCount) {
        try {
            String eventKey = "coupon:event:" + eventId + ":users";
            String userSetKey = "coupon:event:" + eventId + ":issued";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(ISSUE_COUPON_SCRIPT);
            redisScript.setResultType(Long.class);

            Long result = redisTemplate.execute(
                    redisScript,
                    Arrays.asList(userSetKey, userId.toString()),
                    maxCount.toString()
            );

            if (result == null || result == -1) {
                return "SOLD OUT";
            } else if (result == 0) {
                return "ALREADY_ISSUED";
            } else {
                redisTemplate.opsForList().rightPush(
                        "coupon:queue:" + eventId,
                        userId.toString()
                );

                redisTemplate.expire(eventKey, 7, TimeUnit.DAYS);
                redisTemplate.expire(userSetKey, 7, TimeUnit.DAYS);

                return "SUCCESS";
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.COUPON_FAIL);
        }
    }

    private Coupon saveIssuedCoupon(Long userId, Long eventId, CouponEvent event) {
        Coupon coupon = new Coupon(
                UUID.randomUUID().toString(),
                userId,
                eventId,
                event.getDiscountAmount(),
                event.getExpiresAt()
        );

        return couponRepository.save(coupon);
    }

    private Coupon issueCouponWithDatabase(Long userId, Long eventId, CouponEvent event) {
        if (couponRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        event.issueCoupon();
        couponRepository.saveEvent(event);

        return saveIssuedCoupon(userId, eventId, event);
    }

    @Override
    public void validateCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.isUsed()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
    }

    @Override
    public BigDecimal getCouponDiscountAmount(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.isUsed()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        return coupon.getDiscountAmount();
    }

    @Transactional
    public void processCouponQueue(Long eventId) {
        if (redisTemplate == null) return;

        String queueKey = "coupon:queue:" + eventId;
        String userIdStr;

        while ((userIdStr = redisTemplate.opsForList().leftPop(queueKey)) != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                CouponEvent event = couponRepository.findEventById(eventId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

                if (couponRepository.findByUserIdAndEventId(userId, eventId).isEmpty()) {
                    saveIssuedCoupon(userId, eventId, event);

                    event.setRemainingQuantity(event.getRemainingQuantity() - 1);
                    couponRepository.saveEvent(event);
                }
            } catch (Exception e) {
                redisTemplate.opsForList().rightPush("coupon:failed:" + eventId, userIdStr);
            }
        }
    }

}













