package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.application.port.out.CouponEventJpaRepository;
import com.hhplus.ecommerce.coupon.application.port.out.CouponJpaRepository;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 역할: coupon 리포지토리 어댑터 클래스 (Outbound Adapter)
 * 책임: CouponRepository를 구현하며, 실제 DB와 연결하여 데이터 영속화 및 도메인-JPA 엔티티 간 변환
 */

@Repository
public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponEventJpaRepository couponEventJpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository couponJpaRepository, CouponEventJpaRepository couponEventJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponEventJpaRepository = couponEventJpaRepository;
    }


    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponJpaRepository.findById(code)
                .map(CouponJpaEntity::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.from(coupon);
        CouponJpaEntity saved = couponJpaRepository.save(entity);

        return saved.toDomain();
    }

    @Override
    public Optional<CouponEvent> findEventById(Long eventId) {
        return couponEventJpaRepository.findById(eventId)
                .map(CouponEventJpaEntity::toDomain);
    }

    @Override
    public CouponEvent saveEvent(CouponEvent event) {
        CouponEventJpaEntity entity = CouponEventJpaEntity.from(event);
        CouponEventJpaEntity saved = couponEventJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Coupon> findByUserIdAndEventId(Long userId, Long eventId) {
        return couponJpaRepository.findByUserIdAndCouponEventId(userId, eventId)
                .map(CouponJpaEntity::toDomain);
    }

}













