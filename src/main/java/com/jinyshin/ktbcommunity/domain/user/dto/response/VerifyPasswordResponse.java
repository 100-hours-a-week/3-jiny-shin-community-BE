package com.jinyshin.ktbcommunity.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 확인 응답")
public record VerifyPasswordResponse(
    @Schema(description = "비밀번호 일치 여부", example = "true")
    boolean valid
) {
}
