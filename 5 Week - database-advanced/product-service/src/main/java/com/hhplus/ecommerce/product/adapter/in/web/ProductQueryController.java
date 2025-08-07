package com.hhplus.ecommerce.product.adapter.in.web;

import com.hhplus.ecommerce.product.application.port.in.ProductQueryUseCase;
import com.hhplus.ecommerce.product.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 역할: Product Query 컨트롤러
 * 책임: 상품 조회 요청을 처리하는 Read 전용 API 엔드포인트
 */

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final ProductQueryUseCase productQueryUseCase;

    public ProductQueryController(ProductQueryUseCase productQueryUseCase) {
        this.productQueryUseCase = productQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productQueryUseCase.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable long productId) {
        return ResponseEntity.ok(productQueryUseCase.getProduct(productId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopular(@RequestParam(defaultValue = "3") int days,
                                                    @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productQueryUseCase.getPopularProducts(days, limit));
    }

}










