package com.hhplus.ecommerce.order.application.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.OrderRepository;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderCoupon;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.LockTimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final BalancePort balancePort;

    // 사용자별 결제 처리용 Lock (동일 사용자의 동시 결제 방지)
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    // 상품별 재고 처리용 Lock
    private final ConcurrentHashMap<Long, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    // 쿠폰별 사용 처리용 Lock
    private final ConcurrentHashMap<String, ReentrantLock> couponLocks = new ConcurrentHashMap<>();

    public OrderService(OrderRepository orderRepository, ProductPort productPort, CouponPort couponPort, BalancePort balancePort) {
        this.orderRepository = orderRepository;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.balancePort = balancePort;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, List<String> couponCodes) {
        List<OrderItem> items = new ArrayList<>();

        IntStream.range(0, productIds.size()).forEach(i -> {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            // 상품별 Lock 획득
            ReentrantLock productLock = productLocks.computeIfAbsent(productId, k -> new ReentrantLock());
            productLock.lock();
            try {
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
            } finally {
                productLock.unlock();
            }
        });

        // 쿠폰 처리 시 Lock 적용
        List<OrderCoupon> coupons = new ArrayList<>();
        couponCodes.forEach(code -> {
            ReentrantLock couponLock = couponLocks.computeIfAbsent(code, k -> new ReentrantLock());
            couponLock.lock();
            try {
                couponPort.validateCoupon(code);
                BigDecimal discountAmount = couponPort.getCouponDiscountAmount(code);
                coupons.add(new OrderCoupon(code, discountAmount));
            } finally {
                couponLock.unlock();
            }
        });

        Order order = new Order(userId, items, coupons);
        order.confirm();

        return orderRepository.save(order);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Order payOrder(Long orderId) {
        try {
            // 비관적 락으로 주문 조회 (타임아웃 설정됨)
            Order order = orderRepository.findByIdWithPessimisticLock(orderId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

            if (!"CONFIRMED".equals(order.getStatus())) {
                throw new BusinessException(ErrorCode.ORDER_FAIL);
            }

            BigDecimal totalPrice = order.getTotalPrice();

            // 잔액 차감
            balancePort.deductBalance(order.getUserId(), totalPrice);

            // 재고 차감
            order.getItems().forEach(item -> {
                OrderProduct orderProduct = orderRepository.findOrderProductById(item.getOrderProductId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
                productPort.deductStock(orderProduct.getProductId(), item.getQuantity());
            });

            // 쿠폰 사용 처리
            order.getCoupons().forEach(coupon -> coupon.setUsed(true));

            order.pay();
            return orderRepository.save(order);
        } catch (BusinessException e) {
            throw e;
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(ErrorCode.LOCK_ERROR);
        } catch (Exception e) {
            // 결제 실패 시 롤백
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.fail();
                orderRepository.save(order);
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "결제 실패: " + e.getMessage());
        }
    }

}













