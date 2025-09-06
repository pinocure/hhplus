package com.hhplus.ecommerce.common.lock;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnBean(RedissonClient.class)
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String lockKey = generateLockKey(distributedLock.key(), method, joinPoint.getArgs());

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!acquired) {
                log.warn("키에 대한 락 획득 실패 : {}", lockKey);
                throw new BusinessException(ErrorCode.LOCK_ERROR, "잠시 후 다시 시도해주세요");
            }

            log.debug("키에 대한 락 획득 : {}", lockKey);
            return joinPoint.proceed();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.LOCK_ERROR, "락 획득 중 충돌 발생");
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                log.error("키에 대한 락 해제 실패 : {}", lockKey, e);
            }
        }
    }

    private String generateLockKey(String keyExpression, Method method, Object[] args) {
        if (!keyExpression.contains("#")) {
            return keyExpression;
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();

            context.setVariable("arg" + i, args[i]);
            context.setVariable("p" + i, args[i]);

            // 파라미터 이름으로도 설정 (컴파일 옵션에 -parameters가 있을 때만 정상 작동)
            context.setVariable(paramName, args[i]);
        }

        try {
            return parser.parseExpression(keyExpression, new TemplateParserContext()).getValue(context, String.class);
        } catch (Exception e) {
            log.error("Lock key 생성 실패. keyExpression: {}, args: {}", keyExpression, args, e);

            return keyExpression.replace("#={", "").replace("}", "") + ":" + System.currentTimeMillis();
        }
    }

    private static class TemplateParserContext implements org.springframework.expression.ParserContext {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "#={";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    }

}














