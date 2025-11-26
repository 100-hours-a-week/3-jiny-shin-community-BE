package com.jinyshin.ktbcommunity.domain.image.dto.response;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "이미지 메타데이터 응답")
public record ImageMetadataResponse(
    @Schema(description = "이미지 ID", example = "1")
    Long imageId,

    @Schema(description = "저장된 파일명", example = "550e8400-e29b-41d4-a716-446655440000")
    String storedFilename,

    @Schema(description = "원본 파일 확장자", example = "jpg")
    String originalExtension,

    @Schema(description = "S3 저장 경로", example = "images/profile/2025/01")
    String s3Path,

    @Schema(description = "이미지 타입", example = "PROFILE")
    ImageType imageType,

    @Schema(description = "생성 일시", example = "2025-01-26T12:00:00")
    LocalDateTime createdAt
) {

}