package com.jinyshin.ktbcommunity.domain.user.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_PATTERN;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_PATTERN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
        String nickname,

        @NotBlank(message = PASSWORD_MESSAGE)
        @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
        String password,

        Long profileImageId
) {
}