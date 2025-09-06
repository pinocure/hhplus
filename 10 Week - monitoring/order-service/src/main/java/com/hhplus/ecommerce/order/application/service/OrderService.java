package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.common.event.*;
import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ConcurrentMap<Long, OrderProcessingStatus> processingStatus = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();

        IntStream.range(0, productIds.size()).forEach(i -> {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            OrderProduct orderProduct = new OrderProduct(
                    productId,
                    "Product-" + productId,
                    BigDecimal.ZERO
            );

            OrderProduct savedOrderProduct = orderRepository.saveOrderProduct(orderProduct);

            OrderItem orderItem = new OrderItem(
                    savedOrderProduct.getId(),
                    quantity,
                    BigDecimal.ZERO
            );

            items.add(orderItem);
        });

        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(code -> {
            coupons.add(new OrderCoupon(code, BigDecimal.ZERO));
        });

        Order order = new Order(userId, items, coupons);
        order.confirm();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_FAIL);
        }

        processingStatus.put(orderId, new OrderProcessingStatus());


        UserValidationRequestEvent userValidation = new UserValidationRequestEvent(
                orderId, order.getUserId(), LocalDateTime.now()
        );
        kafkaTemplate.send("user-validation-request", userValidation);

        OrderPaymentRequestEvent paymentRequest = new OrderPaymentRequestEvent(
                orderId,
                order.getUserId(),
                order.getTotalPrice(),
                convertToOrderItemInfos(order.getItems()),
                extractCouponCodes(order.getCoupons()),
                LocalDateTime.now()
        );
        kafkaTemplate.send("order-payment-request", paymentRequest);

        order.setStatus("PROCESSING");
        return orderRepository.save(order);
    }


    // 사용자 검증 완료 처리
    @KafkaListener(topics = "user-validation-completed", groupId = "order-service-group")
    @Transactional
    public void handleUserValidationCompleted(UserValidationCompletedEvent event) {
        OrderProcessingStatus status = processingStatus.get(event.getOrderId());
        if (status != null) {
            status.setUserValidated(event.isSuccess());
            checkOrderCompletion(event.getOrderId(), status);
        }
    }


    // 쿠폰 검증 완료 처리
    @KafkaListener(topics = "coupon-validation-completed", groupId = "order-service-group")
    @Transactional
    public void handleCouponValidationCompleted(CouponValidationCompletedEvent event) {
        OrderProcessingStatus status = processingStatus.get(event.getOrderId());
        if (status != null) {
            status.setCouponValidated(event.isSuccess());
            status.setTotalDiscount(event.getTotalDiscount());
            checkOrderCompletion(event.getOrderId(), status);
        }
    }


    // 결제 완료 처리
    @KafkaListener(topics = "balance-payment-completed", groupId = "order-service-group")
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        OrderProcessingStatus status = processingStatus.get(event.getOrderId());
        if (status != null) {
            status.setPaymentCompleted(event.isSuccess());
            checkOrderCompletion(event.getOrderId(), status);
        }
    }


    // 재고 부족 처리
    @KafkaListener(topics = "product-stock-insufficient", groupId = "order-service-group")
    @Transactional
    public void handleStockInsufficient(StockInsufficientEvent event) {
        OrderProcessingStatus status = processingStatus.get(event.getOrderId());
        if (status != null) {
            status.setStockSufficient(false);
            checkOrderCompletion(event.getOrderId(), status);
        }
    }


    private void checkOrderCompletion(Long orderId, OrderProcessingStatus status) {
        if (!status.isAllCompleted()) {
            return;     // 아직 처리 중
        }

        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) return;

            if (status.isAllSuccessful()) {
                order.pay();
                Order savedOrder = orderRepository.save(order);

                OrderCompletedEvent completedEvent = new OrderCompletedEvent(
                        savedOrder.getId(),
                        savedOrder.getUserId(),
                        savedOrder.getTotalPrice(),
                        LocalDateTime.now()
                );
                kafkaTemplate.send("order-completed", completedEvent);

                log.info("주문 완료: orderId={}", orderId);

            } else {
                order.fail();
                orderRepository.save(order);
                log.warn("주문 실패: orderId={}, status={}", orderId, status);
            }

        } finally {
            processingStatus.remove(orderId);
        }
    }


    // OrderItem → OrderItemInfo 변환
    private List<OrderItemInfo> convertToOrderItemInfos(List<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    OrderProduct orderProduct = orderRepository.findOrderProductById(item.getOrderProductId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

                    return new OrderItemInfo(
                            orderProduct.getProductId(),
                            orderProduct.getName(),
                            item.getQuantity(),
                            item.getUnitPrice()
                    );
                })
                .toList();
    }

    // OrderCoupon에서 쿠폰 코드 추출
    private List<String> extractCouponCodes(List<OrderCoupon> coupons) {
        return coupons.stream()
                .map(OrderCoupon::getCouponCode)
                .toList();
    }

}












