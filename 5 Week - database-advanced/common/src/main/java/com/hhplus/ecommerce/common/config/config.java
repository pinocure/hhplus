package com.hhplus.ecommerce.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 역할: 공통 설정 클래스
 * 책임: MSA 전체에서 사용할 공통 설정(트랜잭션, AOP, 비동기 처리)을 정의
 */

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableAsync
public class config {

    // 공통 설정

}














