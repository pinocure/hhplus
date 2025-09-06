package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.application.port.out.CouponEventJpaRepository;
import com.hhplus.ecommerce.coupon.application.port.out.CouponJpaRepository;
import com.hhplus.ecommerce.coupon.application.port.out.CouponRepository;
import com.hhplus.ecommerce.coupon.domain.Coupon;
import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Coupon> findUnusedCouponsByUserId(Long userId) {
        System.out.println("미사용 쿠폰 조회: userId=" + userId);
        return List.of(); // 임시로 빈 리스트
    }

}













