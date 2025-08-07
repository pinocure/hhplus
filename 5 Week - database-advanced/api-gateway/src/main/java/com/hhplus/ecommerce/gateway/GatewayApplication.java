package com.hhplus.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 역할: API Gateway 애플리케이션 진입점
 * 책임: MSA 환경에서 모든 마이크로서비스로의 단일 진입점을 제공하고,
 *      라우팅, 필터링, 부하분산 등의 게이트웨이 기능을 수행
 */

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}


