package com.jinyshin.ktbcommunity.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @Pattern(regexp = "^\\S+$", message = "닉네임에 띄어쓰기를 사용할 수 없습니다.")
        String nickname,

        Long profileImageId
) {
}
