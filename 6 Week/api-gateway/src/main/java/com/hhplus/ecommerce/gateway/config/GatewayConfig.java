package com.hhplus.ecommerce.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", r -> r.path("/orders/**")
                        .uri("http://localhost:8080"))
                .route("balance-service", r -> r.path("/balances/**")
                        .uri("http://localhost:8081"))
                .route("coupon-service", r -> r.path("/coupons/**")
                        .uri("http://localhost:8082"))
                .route("product-service", r -> r.path("/products/**")
                        .uri("http://localhost:8083"))
                .route("user-service", r -> r.path("/users/**")
                        .uri("http://localhost:8084"))
                .build();
    }

}









