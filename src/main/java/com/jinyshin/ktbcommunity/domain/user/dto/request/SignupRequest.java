package com.jinyshin.ktbcommunity.domain.user.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_PATTERN;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.PASSWORD_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "닉네임 (2-10자, 한글/영문/숫자)", example = "홍길동")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
        String nickname,

        @Schema(description = "비밀번호 (8-20자, 영문/숫자/특수문자 포함)", example = "password123!")
        @NotBlank(message = PASSWORD_MESSAGE)
        @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
        String password,

        @Schema(description = "프로필 이미지 ID (선택)", example = "1")
        Long profileImageId
) {
}