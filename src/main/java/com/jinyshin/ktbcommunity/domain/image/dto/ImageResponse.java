package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import java.time.LocalDateTime;

public record ImageResponse(
    Long imageId,
    ImageType imageType,
    String filename,
    String imageUrl,
    LocalDateTime createdAt
) {

}