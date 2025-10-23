package com.jinyshin.ktbcommunity.global.exception;

public class BadRequestException extends ApiException {

  public BadRequestException(ApiErrorCode errorCode) {
    super(errorCode);
  }

  public static BadRequestException sameAsCurrentPassword() {
    return new BadRequestException(ApiErrorCode.SAME_AS_CURRENT_PASSWORD);
  }
}
