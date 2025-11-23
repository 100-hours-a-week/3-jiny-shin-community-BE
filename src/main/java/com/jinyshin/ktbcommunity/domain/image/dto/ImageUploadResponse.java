package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;

public record ImageUploadResponse(
    Long imageId,
    ImageType imageType,
    String filename,
    String imageUrl
) {

}