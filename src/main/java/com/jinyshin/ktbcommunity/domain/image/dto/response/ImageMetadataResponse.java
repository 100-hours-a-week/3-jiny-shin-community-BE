package com.jinyshin.ktbcommunity.domain.image.dto.response;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import java.time.LocalDateTime;

public record ImageMetadataResponse(
    Long imageId,
    String storedFilename,
    String originalExtension,
    String s3Path,
    ImageType imageType,
    LocalDateTime createdAt
) {

}