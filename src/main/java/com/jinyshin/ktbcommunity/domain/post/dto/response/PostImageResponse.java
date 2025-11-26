package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 이미지 정보")
public record PostImageResponse(
    @Schema(description = "이미지 ID", example = "1")
    Long imageId,

    @Schema(description = "이미지 URL 정보 (JPG, WebP)")
    ImageUrlsResponse imageUrls,

    @Schema(description = "이미지 순서", example = "0")
    Integer position,

    @Schema(description = "대표 이미지 여부", example = "true")
    Boolean isPrimary
) {

}