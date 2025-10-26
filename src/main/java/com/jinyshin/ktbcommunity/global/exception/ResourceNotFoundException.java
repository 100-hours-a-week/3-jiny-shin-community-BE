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

    public static ResourceNotFoundException comment() {
        return new ResourceNotFoundException(ApiErrorCode.COMMENT_NOT_FOUND);
    }

    public static ResourceNotFoundException image() {
        return new ResourceNotFoundException(ApiErrorCode.IMAGE_NOT_FOUND);
    }

    public static ResourceNotFoundException like() {
        return new ResourceNotFoundException(ApiErrorCode.LIKE_NOT_FOUND);
    }
}
