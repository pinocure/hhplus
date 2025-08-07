package com.hhplus.ecommerce.product.application.service;

import com.hhplus.ecommerce.product.application.port.in.ProductCommandUseCase;
import com.hhplus.ecommerce.product.application.port.out.ProductCommandRepository;
import com.hhplus.ecommerce.product.application.port.out.ProductQueryRepository;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 역할: Product Command 서비스
 * 책임: 상품의 상태 변경 로직을 처리하고 도메인 이벤트를 발행 (CQRS Write Model)
 */

@Service
public class ProductCommandService implements ProductCommandUseCase {

    private final ProductCommandRepository commandRepository;
    private final ProductQueryRepository queryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductCommandService(ProductCommandRepository commandRepository,
                                 ProductQueryRepository queryRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.commandRepository = commandRepository;
        this.queryRepository = queryRepository;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public void deductStock(Long productId, int quantity) {
        Product product = queryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        product.setEventPublisher(eventPublisher);
        product.deductStock(quantity);
        commandRepository.save(product);
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, int quantity) {
        Product product = queryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        product.setEventPublisher(eventPublisher);
        product.reserveStock(quantity);
        commandRepository.save(product);
    }

    @Override
    @Transactional
    public void cancelReservation(Long productId, int quantity) {
        Product product = queryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        product.setEventPublisher(eventPublisher);
        product.rollbackReservedStock(quantity);
        commandRepository.save(product);
    }

}









