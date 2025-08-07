package com.hhplus.ecommerce.product.application.event;

import com.hhplus.ecommerce.product.adapter.out.persistence.query.PopularProductReadModel;
import com.hhplus.ecommerce.product.application.port.out.PopularProductRepository;
import com.hhplus.ecommerce.product.domain.event.ProductSoldEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductEventHandler {

    private final PopularProductRepository popularProductRepository;

    public ProductEventHandler(PopularProductRepository popularProductRepository) {
        this.popularProductRepository = popularProductRepository;
    }

    @EventListener
    public void handleProductSoldEvent(ProductSoldEvent event) {
        PopularProductReadModel model = popularProductRepository.findById(event.getProductId())
                .orElse(new PopularProductReadModel(event.getProductId(), event.getName(), event.getPrice(),
                        event.getStock(), 0, LocalDateTime.now()));
        model.increaseSalesCount(event.getQuantity());
        popularProductRepository.save(model);
    }

}









