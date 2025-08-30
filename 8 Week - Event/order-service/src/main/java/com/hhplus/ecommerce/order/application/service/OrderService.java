package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.common.lock.DistributedLock;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final BalancePort balancePort;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ProductPort productPort,
                        CouponPort couponPort,
                        BalancePort balancePort,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.balancePort = balancePort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();

        IntStream.range(0, productIds.size()).forEach(i -> {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            ProductPort.ProductDto productDto = productPort.getProduct(productId);
            if (productDto.getStock() < quantity) {
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
                    quantity,
                    productDto.getPrice()
            );

            items.add(orderItem);
        });

        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(code -> {
            couponPort.validateCoupon(code);
            BigDecimal discountAmount = couponPort.getCouponDiscountAmount(code);
            coupons.add(new OrderCoupon(code, discountAmount));
        });

        Order order = new Order(userId, items, coupons);
        order.confirm();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @DistributedLock(key = "lock:order:pay:user:#={p0}", waitTime = 5, leaseTime = 5)
    public Order payOrder(Long orderId) {

        List<StockDeduction> stockDeductions = new ArrayList<>();

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

            if (!"CONFIRMED".equals(order.getStatus())) {
                throw new BusinessException(ErrorCode.ORDER_FAIL);
            }

            BigDecimal totalPrice = order.getTotalPrice();

            balancePort.deductBalance(order.getUserId(), totalPrice);

            for (OrderItem item : order.getItems()) {
                OrderProduct orderProduct = orderRepository.findOrderProductById(item.getOrderProductId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

                stockDeductions.add(new StockDeduction(orderProduct.getProductId(), item.getQuantity()));

                productPort.deductStock(orderProduct.getProductId(), item.getQuantity());
            }

            order.getCoupons().forEach(coupon -> coupon.setUsed(true));

            order.pay();
            Order savedOrder = orderRepository.save(order);

            eventPublisher.publishEvent(new OrderCompletedEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    savedOrder.getTotalPrice(),
                    LocalDateTime.now()
            ));

            return savedOrder;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            rollbackOrder(orderId, stockDeductions);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "결제 실패: " + e.getMessage());
        }
    }


    private void rollbackOrder(Long orderId, List<StockDeduction> stockDeductions) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);

            if (order != null) {
                order.fail();

                for (StockDeduction deduction : stockDeductions) {
                    try {
                        productPort.restoreStock(deduction.productId, deduction.quantity);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                orderRepository.save(order);
            }
        } catch (Exception e ) {
            System.err.println(e.getMessage());
        }
    }


    private static class StockDeduction {
        private final Long productId;
        private final Integer quantity;

        public StockDeduction(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }


}












