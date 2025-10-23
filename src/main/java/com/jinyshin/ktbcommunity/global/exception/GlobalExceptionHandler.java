package com.jinyshin.ktbcommunity.global.exception;

import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
    log.error("ApiException: {}", e.getMessage());

    HttpStatus status = e.getErrorCode().getStatus();
    ApiResponse<Void> response = e.getValidationErrors() != null
        ? ApiResponse.error(e.getErrorCode().getMessage(), e.getValidationErrors())
        : ApiResponse.error(e.getErrorCode().getMessage());

    return new ResponseEntity<>(response, status);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException e) {
    log.error("Validation error: {}", e.getMessage());

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ApiResponse<Void> response = ApiResponse.error("invalid_request", errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
      BadCredentialsException e) {
    log.error("BadCredentialsException: {}", e.getMessage());
    ApiResponse<Void> response = ApiResponse.error("invalid_credentials");
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
      AuthenticationException e) {
    log.error("AuthenticationException: {}", e.getMessage());
    ApiResponse<Void> response = ApiResponse.error("unauthorized_access");
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
    log.error("Unexpected error: ", e);
    ApiResponse<Void> response = ApiResponse.error("internal_server_error");
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}