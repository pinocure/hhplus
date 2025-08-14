package com.hhplus.ecommerce.order.adapter.out.persistence;

import com.hhplus.ecommerce.order.domain.OrderCoupon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCouponJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 50)
    private String couponCode;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private Boolean used;

    @Version
    @Column(nullable = false)
    private Long version = 0L;


    public OrderCouponJpaEntity(Long id, Long orderId, String couponCode, BigDecimal discountAmount, Boolean used) {
        this.id = id;
        this.orderId = orderId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.used = used;
    }

    public static OrderCouponJpaEntity from(OrderCoupon orderCoupon) {
        return new OrderCouponJpaEntity(
                orderCoupon.getId(),
                orderCoupon.getOrderId(),
                orderCoupon.getCouponCode(),
                orderCoupon.getDiscountAmount(),
                orderCoupon.isUsed()
        );
    }

    public OrderCoupon toDomain() {
        OrderCoupon coupon = new OrderCoupon(this.couponCode, this.discountAmount);
        coupon.setId(this.id);
        coupon.setOrderId(this.orderId);
        coupon.setUsed(this.used);
        return coupon;
    }

}












