package com.jinyshin.ktbcommunity.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "invalid_request"),
  SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "same_as_current_password"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "unauthorized_access"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "invalid_credentials"),
  INVALID_CURRENT_PASSWORD(HttpStatus.FORBIDDEN, "invalid_current_password"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "forbidden"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user_not_found"),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "email_already_exists"),
  NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "nickname_already_exists"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error");
  
  private final HttpStatus status;
  private final String message;

  ApiErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

}
