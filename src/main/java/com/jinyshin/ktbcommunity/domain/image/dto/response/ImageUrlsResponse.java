package com.jinyshin.ktbcommunity.domain.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 URL 응답")
public record ImageUrlsResponse(
    @Schema(
        description = "JPG 포맷 이미지 URL",
        example = "https://cdn.example.com/images/profile/2025/01/550e8400-e29b-41d4-a716-446655440000.jpg"
    )
    String jpgUrl,

    @Schema(
        description = "WebP 포맷 이미지 URL",
        example = "https://cdn.example.com/images/profile/2025/01/550e8400-e29b-41d4-a716-446655440000.webp"
    )
    String webpUrl
) {

}