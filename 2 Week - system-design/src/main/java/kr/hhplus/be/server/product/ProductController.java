package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllMockProducts() {
        return productService.getAllMockProductList();
    }

    @GetMapping("/popular")
    public List<ProductResponse> getPopularMockProducts() {
        return productService.getMockPopularProducts();
    }

}
