package com.jinyshin.ktbcommunity.domain.image.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImageResponse(
    Long imageId,
    ImageType imageType,
    String storedFilename,
    String originalExtension,
    String jpgUrl,
    String webpUrl,
    LocalDateTime createdAt
) {

}