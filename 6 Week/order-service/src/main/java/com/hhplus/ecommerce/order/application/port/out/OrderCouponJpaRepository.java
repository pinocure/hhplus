package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderCouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderCouponJpaRepository extends JpaRepository<OrderCouponJpaEntity, Long> {

    List<OrderCouponJpaEntity> findByOrderId(Long orderId);

}
