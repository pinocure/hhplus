package com.hhplus.ecommerce.coupon.adapter.out.persistence;

import com.hhplus.ecommerce.coupon.domain.Coupon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponJpaEntity {

    @Id
    @Column(length = 50)
    private String code;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponEventId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used;

    public CouponJpaEntity(String code,
                           Long userId,
                           Long couponEventId,
                           BigDecimal discountAmount,
                           LocalDateTime issuedAt,
                           LocalDateTime expiresAt,
                           Boolean used) {
        this.code = code;
        this.userId = userId;
        this.couponEventId = couponEventId;
        this.discountAmount = discountAmount;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public static CouponJpaEntity from(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getCode(),
                coupon.getUserId(),
                coupon.getCouponEventId(),
                coupon.getDiscountAmount(),
                coupon.getIssuedAt(),
                coupon.getExpiresAt(),
                coupon.isUsed()
        );
    }

    public Coupon toDomain() {
        Coupon coupon = new Coupon(this.code, this.userId, this.couponEventId, this.discountAmount, this.expiresAt);
        coupon.setIssuedAt(this.getIssuedAt());
        coupon.setUsed(this.used);
        return coupon;
    }


}








