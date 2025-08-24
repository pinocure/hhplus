package com.hhplus.ecommerce.common.exception;

public enum ErrorCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR("E001", "내부 서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE("E002", "잘못된 입력값입니다."),
    LOCK_ERROR("E003", "잠금관련 오류가 발생했습니다."),

    // 사용자 관련
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),

    // 잔액 관련
    INSUFFICIENT_BALANCE("B001", "잔액이 부족합니다."),
    BALANCE_NOT_FOUND("B002", "잔액 정보가 없습니다."),

    // 상품 관련
    PRODUCT_NOT_FOUND("P001", "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK("P002", "재고가 부족합니다."),
    PRODUCT_FINISH("P003", "상품이 품절되었습니다."),
    INSUFFICIENT_RESERCE_STOCK("P004", "예약 재고가 부족합니다."),


    // 쿠폰 관련
    COUPON_NOT_FOUND("C001", "쿠폰을 찾을 수 없습니다."),
    COUPON_ALREADY_USED("C002", "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED("C003", "만료된 쿠폰입니다."),
    COUPON_SOLD_OUT("C004", "쿠폰이 모두 발급되었습니다."),
    COUPON_FAIL("C005", "쿠폰 발급에 실패했습니다."),
    COUPON_ALREADY_GET("C006", "이미 쿠폰을 발급받았습니다."),

    // 주문 관련
    ORDER_NOT_FOUND("O001", "주문 정보를 찾을 수 없습니다."),
    ORDER_INVALID_STATUS("O002", "주문 상태가 올바르지 않습니다."),
    ORDER_FAIL("O003", "주문에 실패했습니다."),
    ORDER_EVENT_ERROR("0004", "이벤트 관련 에러가 발생했습니다."),
    ORDER_EVENT_FAIL("0005", "이벤트 처리 중 실패하였습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}







