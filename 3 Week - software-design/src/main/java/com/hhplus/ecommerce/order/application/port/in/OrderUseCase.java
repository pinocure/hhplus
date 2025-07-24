package com.hhplus.ecommerce.order.application.port.in;

import com.hhplus.ecommerce.order.domain.Order;

import java.util.List;

/**
 * 역할: order 도메인의 입력 포트 인터페이스
 * 책임: 주문 관련 유즈케이스(생성, 결제)를 정의하여 애플리케이션 서비스가 이를 구현하도록 함.
 */

public interface OrderUseCase {

    Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes);

    Order payOrder(Long orderId);
}
