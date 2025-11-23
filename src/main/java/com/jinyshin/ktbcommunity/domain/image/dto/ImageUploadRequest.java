package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ImageUploadRequest(
    @NotBlank(message = "storedFilename은 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "파일명은 영문, 숫자, -, _만 가능합니다")
    String storedFilename,

    @NotBlank(message = "originalExtension은 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "확장자는 영문, 숫자만 가능합니다")
    @Size(max = 10, message = "확장자는 10자 이하여야 합니다")
    String originalExtension,

    @NotNull(message = "imageType은 필수입니다")
    ImageType imageType
) {

}
