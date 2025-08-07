package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.domain.CouponEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 역할: CouponEvent JPA 엔티티 클래스
 * 책임: 쿠폰 이벤트 테이블과 매핑되는 JPA 엔티티로, 낙관적 락을 지원하여 동시성 제어
 */

@Entity
@Table(name = "coupon_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEventJpaEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer remainingQuantity;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private Long version;

    public CouponEventJpaEntity(Long id,
                                String name,
                                BigDecimal discountAmount,
                                Integer totalQuantity,
                                Integer remainingQuantity,
                                LocalDateTime expiresAt,
                                Long version) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.expiresAt = expiresAt;
        this.version = version != null ? version : 0L;
    }

    public static CouponEventJpaEntity from(CouponEvent couponEvent) {
        return new CouponEventJpaEntity(
                couponEvent.getId(),
                couponEvent.getName(),
                couponEvent.getDiscountAmount(),
                couponEvent.getTotalQuantity(),
                couponEvent.getRemainingQuantity(),
                couponEvent.getExpiresAt(),
                couponEvent.getVersion()
        );
    }

    public CouponEvent toDomain() {
        CouponEvent event = new CouponEvent(this.id, this.name, this.discountAmount, this.totalQuantity, this.expiresAt);
        event.setRemainingQuantity(this.remainingQuantity);
        event.setVersion(this.version);
        return event;
    }

}












