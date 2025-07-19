package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductServiceTest {

    private final ProductService productService = new ProductService();

    @Test
    @DisplayName("전체 상품 MOCK 데이터 조회")
    void getAllMockListTest() {
        List<ProductResponse> products = productService.getAllMockProductList();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).name()).isEqualTo("아이폰");
        assertThat(products.get(1).price()).isEqualTo(new BigDecimal("2000"));
    }

    @Test
    @DisplayName("인기 상품 5개 조회")
    void getMockPopularProductsTest() {
        List<ProductResponse> products = productService.getMockPopularProducts();

        assertThat(products).hasSize(5);
        assertThat(products.get(0).name()).isEqualTo("갤럭시S20");
        assertThat(products.get(2).price()).isEqualTo(new BigDecimal("3000"));
        assertThat(products.get(4).stock()).isEqualTo(500);
    }

}
