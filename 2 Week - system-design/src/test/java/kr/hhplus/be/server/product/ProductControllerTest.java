package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("전체 상품 조회 API는 mock 리스트 반환")
    void getAllMockProducts() throws Exception {
        given(productService.getAllMockProductList()).willReturn(
                List.of(
                        new ProductResponse(1L, "아이폰", new BigDecimal("1000"), 100),
                        new ProductResponse(2L, "갤럭시", new BigDecimal("2000"), 200)
                )
        );

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("아이폰"))
                .andExpect(jsonPath("$[1].price").value(2000))
                .andExpect(jsonPath("$[1].stock").value(200));
    }

    @Test
    @DisplayName("인기 상품 조회 API는 mock 리스트 5개 반환")
    void getPopularMockProducts() throws Exception {
        given(productService.getMockPopularProducts()).willReturn(
                List.of(new ProductResponse(1L, "갤럭시S20", new BigDecimal("1000"), 100),
                        new ProductResponse(2L, "갤럭시S21", new BigDecimal("2000"), 200),
                        new ProductResponse(3L, "갤럭시S22", new BigDecimal("3000"), 300),
                        new ProductResponse(4L, "갤럭시S23", new BigDecimal("4000"), 400),
                        new ProductResponse(5L, "갤럭시S24", new BigDecimal("5000"), 500)
                )
        );

        mockMvc.perform(get("/products/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].name").value("갤럭시S21"))
                .andExpect(jsonPath("$[2].stock").value(300))
                .andExpect(jsonPath("$[3].id").value(4L))
                .andExpect(jsonPath("$[4].name").value("갤럭시S24"));
    }

}











