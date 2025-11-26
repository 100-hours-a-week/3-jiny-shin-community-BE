package com.jinyshin.ktbcommunity.domain.user.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 수정 요청")
public record ProfileUpdateRequest(
    @Schema(description = "닉네임 (2-10자, 한글/영문/숫자, 선택)", example = "홍길동")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
    String nickname,

    @Schema(description = "프로필 이미지 ID (선택)", example = "1")
    Long profileImageId
) {

}
