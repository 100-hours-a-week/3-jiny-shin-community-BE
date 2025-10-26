package com.jinyshin.ktbcommunity.global.exception;

public class ConflictException extends ApiException{

    public ConflictException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    public static ConflictException emailAlreadyExists() {
        return new ConflictException(ApiErrorCode.EMAIL_ALREADY_EXISTS);
    }

    public static ConflictException nicknameAlreadyExists() {
        return new ConflictException(ApiErrorCode.NICKNAME_ALREADY_EXISTS);
    }
}
