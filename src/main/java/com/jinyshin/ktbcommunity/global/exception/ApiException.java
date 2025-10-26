package com.jinyshin.ktbcommunity.global.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;
    private final Map<String, String> validationErrors;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.validationErrors = null;
    }

    public ApiException(ApiErrorCode errorCode, Map<String, String> validationErrors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.validationErrors = validationErrors;
    }
}
