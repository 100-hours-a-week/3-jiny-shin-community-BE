package com.jinyshin.ktbcommunity.global.exception;

public class UnauthorizedException extends ApiException {

  public UnauthorizedException(ApiErrorCode errorCode) {
    super(errorCode);
  }

  public static UnauthorizedException invalidCredentials() {
    return new UnauthorizedException(ApiErrorCode.INVALID_CREDENTIALS);
  }

  public static UnauthorizedException forbidden() {
    return new UnauthorizedException(ApiErrorCode.FORBIDDEN);
  }
}
