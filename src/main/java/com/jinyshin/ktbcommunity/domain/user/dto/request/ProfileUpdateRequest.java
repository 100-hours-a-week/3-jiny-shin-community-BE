package com.jinyshin.ktbcommunity.domain.user.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_MESSAGE;
import static com.jinyshin.ktbcommunity.global.constants.ValidationPatterns.NICKNAME_PATTERN;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE)
    String nickname,

    Long profileImageId
) {

}
