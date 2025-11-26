package com.jinyshin.ktbcommunity.global.exception;

import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<BaseResponse<Void>> handleApiException(ApiException e) {
    log.error("ApiException: {}", e.getMessage());

    HttpStatus status = e.getErrorCode().getStatus();
    BaseResponse<Void> response = e.getValidationErrors() != null
        ? BaseResponse.error(e.getErrorCode().getMessage(), e.getValidationErrors())
        : BaseResponse.error(e.getErrorCode().getMessage());

    return new ResponseEntity<>(response, status);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Void>> handleValidationException(
      MethodArgumentNotValidException e) {
    log.error("Validation error: {}", e.getMessage());

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    BaseResponse<Void> response = BaseResponse.error("invalid_request", errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception e) {
    log.error("Unexpected error: ", e);
    BaseResponse<Void> response = BaseResponse.error("internal_server_error");
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}