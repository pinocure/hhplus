package com.hhplus.ecommerce.order.application.port.out;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    List<OrderItemJpaEntity> findByOrderId(Long orderId);

}
