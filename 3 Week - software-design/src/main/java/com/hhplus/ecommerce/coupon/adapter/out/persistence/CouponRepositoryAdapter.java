package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 역할: coupon 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: CouponRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화
 */

@Repository
public class CouponRepositoryAdapter implements CouponRepository {

    // In-Memory 구현 -> JPA로 변경해야함
    private final Map<String, Coupon> couponStore = new HashMap<>();
    private final Map<Long, CouponEvent> eventStore = new HashMap<>();

    public CouponRepositoryAdapter() {
        eventStore.put(1L, new CouponEvent(1L, "Event1", new BigDecimal("500"), 10, LocalDateTime.now().plusDays(7)));
    }


    @Override
    public Optional<Coupon> findByCode(String code) {
        return Optional.ofNullable(couponStore.get(code));
    }

    @Override
    public Coupon save(Coupon coupon) {
        couponStore.put(coupon.getCode(), coupon);
        return coupon;
    }

    @Override
    public Optional<CouponEvent> findEventById(Long eventId) {
        return Optional.ofNullable(eventStore.get(eventId));
    }

    @Override
    public CouponEvent saveEvent(CouponEvent event) {
        eventStore.put(event.getId(), event);
        return event;
    }

    @Override
    public Optional<Coupon> findByUserIdAndEventId(Long userId, Long eventId) {
        return couponStore.values().stream()
                .filter(c -> c.getUserId().equals(userId) && c.getCouponEventId().equals(eventId))
                .findFirst();
    }

}













