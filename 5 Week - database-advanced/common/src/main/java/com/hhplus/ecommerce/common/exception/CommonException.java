package com.hhplus.ecommerce.common.exception;

/**
 * 역할: 공통 예외 처리 클래스
 * 책임: MSA 전체에서 사용할 공통 예외 타입과 에러 처리 로직을 정의
 */

public class CommonException extends RuntimeException {

    private final String errorCode;
    private final String serviceName;

    public CommonException(String errorCode, String serviceName, String message) {
        super(message);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }

    public CommonException(String errorCode, String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getServiceName() {
        return serviceName;
    }

}

class BusinessException extends CommonException {
    public BusinessException(String serviceName, String message) {
        super("BUSINESS_ERROR", serviceName, message);
    }
}

class InfrastructureException extends CommonException {
    public InfrastructureException(String serviceName, String message, Throwable cause) {
        super("INFRASTRUCTURE_ERROR", serviceName, message, cause);
    }
}

class ConcurrencyException extends CommonException {
    public ConcurrencyException(String serviceName, String message) {
        super("CONCURRENCY_ERROR", serviceName, message);
    }
}















