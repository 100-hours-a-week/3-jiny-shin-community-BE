package com.jinyshin.ktbcommunity.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

  private final LocalDateTime timestamp;
  private final String message;
  private final T data;
  private final Map<String, String> errors;

  private BaseResponse(LocalDateTime timestamp, String message, T data,
      Map<String, String> errors) {
    this.timestamp = timestamp;
    this.message = message;
    this.data = data;
    this.errors = errors;
  }

  public static <T> BaseResponse<T> success(String message, T data) {
    return new BaseResponse<>(LocalDateTime.now(), message, data, null);
  }

  public static <T> BaseResponse<T> error(String message) {
    return new BaseResponse<>(LocalDateTime.now(), message, null, null);
  }

  public static <T> BaseResponse<T> error(String message, Map<String, String> errors) {
    return new BaseResponse<>(LocalDateTime.now(), message, null, errors);
  }
}
