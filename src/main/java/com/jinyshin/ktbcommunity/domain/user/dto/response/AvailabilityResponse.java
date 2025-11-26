package com.jinyshin.ktbcommunity.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "중복 확인 응답")
public record AvailabilityResponse(
        @Schema(description = "사용 가능 여부 (true: 사용 가능, false: 이미 사용 중)", example = "true")
        boolean available
) {
}
