package com.jinyshin.ktbcommunity.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final LocalDateTime timestamp;
  private final String message;
  private final T data;
  private final Map<String, String> errors;

  private ApiResponse(LocalDateTime timestamp, String message, T data, Map<String, String> errors) {
    this.timestamp = timestamp;
    this.message = message;
    this.data = data;
    this.errors = errors;
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<T>(LocalDateTime.now(), message, data, null);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<T>(LocalDateTime.now(), message, null, null);
  }

  public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
    return new ApiResponse<>(LocalDateTime.now(), message, null, errors);
  }
}