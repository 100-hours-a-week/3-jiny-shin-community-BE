package com.jinyshin.ktbcommunity.domain.image.dto.request;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "이미지 메타데이터 저장 요청")
public record ImageMetadataRequest(
    @Schema(
        description = "저장된 파일명 (UUID 기반)",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotBlank(message = "storedFilename은 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "파일명은 영문, 숫자, -, _만 가능합니다")
    String storedFilename,

    @Schema(
        description = "원본 파일 확장자",
        example = "jpg"
    )
    @NotBlank(message = "originalExtension은 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "확장자는 영문, 숫자만 가능합니다")
    @Size(max = 10, message = "확장자는 10자 이하여야 합니다")
    String originalExtension,

    @Schema(
        description = "S3 저장 경로",
        example = "images/profile/2025/01"
    )
    @NotBlank(message = "s3Path는 필수입니다")
    @Size(max = 100, message = "s3Path는 100자 이하여야 합니다")
    String s3Path,

    @Schema(
        description = "이미지 타입 (PROFILE, POST)",
        example = "PROFILE"
    )
    @NotNull(message = "imageType은 필수입니다")
    ImageType imageType
) {

}
