package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;

public record ImageUploadResponse(
    Long imageId,
    ImageType imageType,
    String storedFilename,
    String originalExtension,
    String originalUrl,
    String jpgUrl,
    String webpUrl
) {

}