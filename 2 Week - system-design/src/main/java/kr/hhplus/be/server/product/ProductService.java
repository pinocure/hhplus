package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.dto.ProductResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    public List<ProductResponse> getAllMockProductList() {
        return List.of(
                new ProductResponse(1L, "아이폰", new BigDecimal("1000"), 100),
                new ProductResponse(2L, "갤럭시", new BigDecimal("2000"), 200)
        );
    }

    public List<ProductResponse> getMockPopularProducts() {
        return List.of(
                new ProductResponse(1L, "갤럭시S20", new BigDecimal("1000"), 100),
                new ProductResponse(2L, "갤럭시S21", new BigDecimal("2000"), 200),
                new ProductResponse(3L, "갤럭시S22", new BigDecimal("3000"), 300),
                new ProductResponse(4L, "갤럭시S23", new BigDecimal("4000"), 400),
                new ProductResponse(5L, "갤럭시S24", new BigDecimal("5000"), 500)
        );
    }

}
