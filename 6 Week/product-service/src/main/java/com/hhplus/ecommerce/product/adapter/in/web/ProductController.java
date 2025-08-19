package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productUseCase.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable long productId) {
        return ResponseEntity.ok(productUseCase.getProduct(productId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopular(@RequestParam(defaultValue = "3") int days,
                                                    @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productUseCase.getPopularProducts(days, limit));
    }

    @PostMapping("/{productId}/deduct")
    public ResponseEntity<Void> deductStock(@PathVariable Long productId, @RequestParam int quantity) {
        productUseCase.deductStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/restore")
    public ResponseEntity<Void> restoreStock(@PathVariable Long productId, @RequestParam int quantity) {
        productUseCase.restoreStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

}










