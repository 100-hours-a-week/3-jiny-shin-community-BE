package com.jinyshin.ktbcommunity.domain.aigeneration.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI 생성 잔여 횟수 응답")
public record AiGenerationRemainingResponse(
    @Schema(description = "남은 횟수", example = "3") long remaining,
    @Schema(description = "하루 한도", example = "5") long limit,
    @Schema(description = "사용한 횟수", example = "2") long used
) {
}
