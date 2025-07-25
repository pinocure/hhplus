package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 역할: product 웹 컨트롤러 클래스 (Inbound Adapter)
 * 책임: 상품 관련 HTTP 요청을 처리하고, ProductUseCase를 호출하여 유즈케이스 흐름을 트리거
 */

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

}










