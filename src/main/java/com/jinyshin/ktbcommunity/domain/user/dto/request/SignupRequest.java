package com.jinyshin.ktbcommunity.domain.user.dto.request;

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
        @Pattern(regexp = "^\\S+$", message = "닉네임에 띄어쓰기를 사용할 수 없습니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 8~20자의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]|\\\\:;\"'<>,.?/]).{8,20}$",
                message = "비밀번호는 8~20자의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        Long profileImageId
) {
}