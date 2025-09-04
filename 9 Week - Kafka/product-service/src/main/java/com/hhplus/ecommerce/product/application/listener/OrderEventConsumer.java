package com.hhplus.ecommerce.product.application.listener;

import com.hhplus.ecommerce.common.event.OrderCompletedEvent;
import com.hhplus.ecommerce.common.event.OrderItemInfo;
import com.hhplus.ecommerce.common.event.OrderPaymentRequestEvent;
import com.hhplus.ecommerce.common.event.StockInsufficientEvent;
import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductRepository;
import com.hhplus.ecommerce.product.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductUseCase productUseCase;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-payment-request", groupId = "product-service-group")
    @Transactional
    public void handleStockDeduction(OrderPaymentRequestEvent event) {
        try {
            for (OrderItemInfo item : event.getItems()) {
                try {
                    productUseCase.deductStock(item.getProductId(), item.getQuantity());
                } catch (Exception e) {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    Integer availableStock = product != null ? product.getStock() : 0;

                    StockInsufficientEvent stockEvent = new StockInsufficientEvent(
                            event.getOrderId(),
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            availableStock
                    );

                    kafkaTemplate.send("product-stock-insufficient", stockEvent);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("재고 차감 실패: orderId={}, error={}", event.getOrderId(), e.getMessage());
        }
    }


    @KafkaListener(topics = "order-completed", groupId = "product-sales-group")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            LocalDate today = event.getOrderedAt().toLocalDate();

            productRepository.updateSalesStatistics(today, event.getTotalPrice());
        } catch (Exception e) {
            log.error("판매 통계 업데이트 실패: {}", e.getMessage());
        }
    }

}







