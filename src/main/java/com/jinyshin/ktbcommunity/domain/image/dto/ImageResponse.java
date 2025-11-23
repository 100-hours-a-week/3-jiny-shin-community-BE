package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import java.time.LocalDateTime;

public record ImageResponse(
    Long imageId,
    ImageType imageType,
    String storedFilename,
    String originalExtension,
    String originalUrl,
    String jpgUrl,
    String webpUrl,
    LocalDateTime createdAt
) {

}