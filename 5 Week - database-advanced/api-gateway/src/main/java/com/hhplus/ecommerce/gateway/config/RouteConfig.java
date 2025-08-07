package com.hhplus.ecommerce.gateway.config;

import com.hhplus.ecommerce.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 역할: API Gateway 라우팅 설정 클래스
 * 책임: 각 마이크로서비스로의 요청 라우팅 규칙을 정의하고,
 *      필요한 필터를 적용하여 요청/응답을 처리
 */

@Configuration
public class RouteConfig {

    @Value("${services.order.url:http://order-service:8080}")
    private String orderServiceUrl;

    @Value("${services.balance.url:http://balance-service:8081}")
    private String balanceServiceUrl;

    @Value("${services.coupon.url:http://coupon-service:8082}")
    private String couponServiceUrl;

    @Value("${services.product.url:http://product-service:8083}")
    private String productServiceUrl;

    @Value("${services.user.url:http://user-service:8084}")
    private String userServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.rewritePath("/api/orders/(?<segment>.*)", "/orders/${segment}"))
                        .uri(orderServiceUrl))
                .route("balance-service", r -> r.path("/api/balances/**")
                        .filters(f -> f.rewritePath("/api/balances/(?<segment>.*)", "/balances/${segment}"))
                        .uri(balanceServiceUrl))
                .route("coupon-service", r -> r.path("/api/coupons/**")
                        .filters(f -> f.rewritePath("/api/coupons/(?<segment>.*)", "/coupons/${segment}"))
                        .uri(couponServiceUrl))
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f.rewritePath("/api/products/(?<segment>.*)", "/products/${segment}"))
                        .uri(productServiceUrl))
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/users/${segment}"))
                        .uri(userServiceUrl))
                .build();
    }

}










