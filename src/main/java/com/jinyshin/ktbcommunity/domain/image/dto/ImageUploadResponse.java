package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;

public record ImageUploadResponse(
    Long imageId,
    String imageType,
    String filename
) {

  public static ImageUploadResponse from(Image image) {
    return new ImageUploadResponse(
        image.getImageId(),
        image.getImageType().name(),
        image.getFilename()
    );
  }
}