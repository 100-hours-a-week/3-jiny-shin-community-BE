package com.jinyshin.ktbcommunity.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordUpdateRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = "비밀번호는 8~20자의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]|\\\\:;\"'<>,.?/]).{8,20}$",
                message = "비밀번호는 8~20자의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        String newPassword
) {
}