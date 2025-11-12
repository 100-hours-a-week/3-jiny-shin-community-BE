package com.jinyshin.ktbcommunity.global.exception;

public class ForbiddenException extends ApiException {

  public ForbiddenException(ApiErrorCode errorCode) {
    super(errorCode);
  }

  public static ForbiddenException invalidCurrentPassword() {
    return new ForbiddenException(ApiErrorCode.INVALID_CURRENT_PASSWORD);
  }

  public static ForbiddenException forbidden() {
    return new ForbiddenException(ApiErrorCode.FORBIDDEN);
  }
}
