package com.jinyshin.ktbcommunity.global.exception;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    public static ResourceNotFoundException user() {
        return new ResourceNotFoundException(ApiErrorCode.USER_NOT_FOUND);
    }

    public static ResourceNotFoundException post() {
        return new ResourceNotFoundException(ApiErrorCode.POST_NOT_FOUND);
    }
}
