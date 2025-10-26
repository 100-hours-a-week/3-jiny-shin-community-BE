package com.jinyshin.ktbcommunity.domain.user.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_PATTERN;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordUpdateRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = PASSWORD_MESSAGE)
        @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
        String newPassword
) {
}