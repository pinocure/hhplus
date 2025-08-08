package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.order.adapter.out.external.adapter.ProductFeignAdapter;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.application.saga.OrderSagaOrchestrator;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 역할: order 서비스 구현 클래스
 * 책임: OrderUseCase를 구현하며, 유즈케이스 흐름을 조율하고 도메인 로직을 호출하며 포트들을 통해 외부와 상호작용.
 *      Saga 패턴을 적용하여 분산 트랜잭션 처리
 */

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final BalancePort balancePort;
    private final OrderSagaOrchestrator orderSagaOrchestrator;

    public OrderService(OrderRepository orderRepository,
                        ProductPort productPort,
                        CouponPort couponPort,
                        BalancePort balancePort,
                        OrderSagaOrchestrator orderSagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.balancePort = balancePort;
        this.orderSagaOrchestrator = orderSagaOrchestrator;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();

        IntStream.range(0, productIds.size()).forEach(i -> {
            ProductFeignAdapter.ProductDto productDto = productPort.getProduct(productIds.get(i));
            if (productDto.getStock() < quantities.get(i)) {
                throw new IllegalArgumentException(productDto.getName() + "의 재고가 부족합니다.");
            }

            OrderProduct orderProduct = new OrderProduct(
                    productDto.getId(),
                    productDto.getName(),
                    productDto.getPrice()
            );

            OrderProduct savedOrderProduct = orderRepository.saveOrderProduct(orderProduct);

            OrderItem orderItem = new OrderItem(
                    savedOrderProduct.getId(),
                    quantities.get(i),
                    productDto.getPrice()
            );

            items.add(orderItem);
        });

        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(couponCode -> {
            couponPort.validateCoupon(couponCode);
            BigDecimal discountAmount = couponPort.getCouponDiscountAmount(couponCode);
            OrderCoupon orderCoupon = new OrderCoupon(couponCode, discountAmount);
            coupons.add(orderCoupon);
        });

        Order order = new Order(userId, items, coupons);
        Order savedOrder = orderRepository.save(order);

        try {
            orderSagaOrchestrator.startOrderSaga(savedOrder);
            savedOrder.setStatus("CONFIRMED");
            return orderRepository.save(savedOrder);
        } catch (Exception e) {
            savedOrder.fail();
            orderRepository.save(savedOrder);
            throw e;
        }
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. 관리자에게 문의해주세요."));

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }

        // Saga 패턴으로 결제 처리
        try {
            orderSagaOrchestrator.startPaymentSaga(order);
            return order;
        } catch (Exception e) {
            // 결제 실패 시 주문 상태를 FAILED로 변경
            order.fail();
            orderRepository.save(order);
            throw e;
        }
    }

}













