package com.jinyshin.ktbcommunity.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 성공과 에러 응답 모두에 사용되는 공통 응답 구조입니다.
 * {@code data} 또는 {@code errors} 중 하나만 값이 채워집니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String message,
        T data,
        Object errors
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(message, null, null);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(message, null, null);
    }

    public static ApiResponse<Void> error(String message, Object errors) {
        return new ApiResponse<>(message, null, errors);
    }
}
