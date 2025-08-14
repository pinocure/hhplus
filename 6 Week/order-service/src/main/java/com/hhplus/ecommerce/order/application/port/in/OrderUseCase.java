package com.hhplus.ecommerce.order.application.port.in;

import com.hhplus.ecommerce.order.domain.Order;

import java.util.List;

public interface OrderUseCase {

    Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes);

    Order payOrder(Long orderId);
}
