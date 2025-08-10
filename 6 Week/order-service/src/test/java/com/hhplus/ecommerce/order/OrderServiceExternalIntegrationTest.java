package com.hhplus.ecommerce.order;

import com.hhplus.ecommerce.order.adapter.out.persistence.OrderRepositoryAdapter;
import com.hhplus.ecommerce.order.application.port.in.OrderUseCase;
import com.hhplus.ecommerce.order.application.port.out.feign.BalancePort;
import com.hhplus.ecommerce.order.application.port.out.feign.CouponPort;
import com.hhplus.ecommerce.order.application.port.out.feign.ProductPort;
import com.hhplus.ecommerce.order.domain.Order;
import com.hhplus.ecommerce.order.domain.OrderItem;
import com.hhplus.ecommerce.order.domain.OrderProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Order Service 외부 협력 통합 테스트
 */

@SpringBootTest
@Testcontainers
@Transactional
public class OrderServiceExternalIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("orders")
            .withUsername("ruang")
            .withPassword("ruang");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private OrderUseCase orderUseCase;

    // Mock으로 외부 서비스 진행
    @MockitoBean
    private BalancePort balancePort;

    @MockitoBean
    private ProductPort productPort;

    @MockitoBean
    private CouponPort couponPort;

    @MockitoBean
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @BeforeEach
    void setUp() {
        setupMockBehaviors();
    }


    // Mock 기본 동작
    private void setupMockBehaviors() {
        // balance port
        when(balancePort.getBalance(anyLong())).thenReturn(new BigDecimal("3000000"));
        doNothing().when(balancePort).deductBalance(anyLong(), any(BigDecimal.class));


        // product port - 에어팟
        ProductPort.ProductDto airpodsDto = new ProductPort.ProductDto();
        airpodsDto.setId(4L);
        airpodsDto.setName("에어팟");
        airpodsDto.setPrice(new BigDecimal("200000"));
        airpodsDto.setStock(20);

        when(productPort.getProduct(4L)).thenReturn(airpodsDto);
        doNothing().when(productPort).deductStock(anyLong(), anyInt());

        // product port - 아이폰
        ProductPort.ProductDto iphoneDto = new ProductPort.ProductDto();
        iphoneDto.setId(1L);
        iphoneDto.setName("아이폰 15");
        iphoneDto.setPrice(new BigDecimal("1000000"));
        iphoneDto.setStock(10);

        when(productPort.getProduct(1L)).thenReturn(iphoneDto);


        // coupon port - 쿠폰없음으로 설정
        doNothing().when(couponPort).validateCoupon(anyString());
        when(couponPort.getCouponDiscountAmount(anyString())).thenReturn(new BigDecimal("50000"));


        OrderProduct mockOrderProduct = new OrderProduct(4L, "에어팟", new BigDecimal("200000"));
        mockOrderProduct.setId(1L);

        when(orderRepositoryAdapter.saveOrderProduct(any(OrderProduct.class))).thenReturn(mockOrderProduct);

        Order mockOrder = new Order(1L, List.of(new OrderItem(1L, 2, new BigDecimal("200000"))), List.of());
        mockOrder.setId(1L);
        mockOrder.confirm();
        when(orderRepositoryAdapter.save(any(Order.class))).thenReturn(mockOrder);

        when(orderRepositoryAdapter.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(orderRepositoryAdapter.findByIdWithPessimisticLock(anyLong())).thenReturn(Optional.of(mockOrder));
        when(orderRepositoryAdapter.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.of(mockOrder));
        when(orderRepositoryAdapter.findOrderProductById(anyLong())).thenReturn(Optional.of(mockOrderProduct));
    }

    @Test
    @DisplayName("주문 생성 및 결제 완료")
    public void completeOrderFlow() {
        // given : 주문 정보
        Long userId = 1L;
        Long productId = 4L;
        Integer quantity = 2;


        // when : 주문 생성
        Order createdOrder = orderUseCase.createOrder(
                userId,
                List.of(productId),
                List.of(quantity),
                List.of()           // 쿠폰 없음
        );


        // then : 주문 생성 검증
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("CONFIRMED", createdOrder.getStatus());

        // 외부 서비스 호출
        verify(productPort).getProduct(productId);


        // when : 결제 처리
        Order paidOrder = orderUseCase.payOrder(createdOrder.getId());

        // then : 결제 완료
        assertEquals("PAID", paidOrder.getStatus());
        assertEquals(createdOrder.getId(), paidOrder.getId());

        // 외부 서비스
        verify(balancePort).deductBalance(userId, new BigDecimal("400000"));    // 40만
        verify(productPort).deductStock(productId, quantity);

        System.out.println("pinocure님 step07이 완료되었습니다.");
    }

}








