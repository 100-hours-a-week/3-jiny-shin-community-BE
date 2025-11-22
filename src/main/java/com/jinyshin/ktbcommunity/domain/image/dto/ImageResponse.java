package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;

public record ImageResponse(
    Long imageId,
    String imageType,
    String filename,
    String createdAt
) {

  public static ImageResponse from(Image image) {
    return new ImageResponse(
        image.getImageId(),
        image.getImageType().name(),
        image.getFilename(),
        image.getCreatedAt().toString()
    );
  }
}