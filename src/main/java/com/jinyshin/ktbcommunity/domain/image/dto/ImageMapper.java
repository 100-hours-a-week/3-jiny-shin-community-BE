package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;

public final class ImageMapper {

  private ImageMapper() {
  }

  /**
   * Image Entity → ImageResponse DTO 변환
   *
   * @param image       Image 엔티티
   * @param originalUrl 원본 이미지 URL
   * @param jpgUrl      JPG 포맷 URL
   * @param webpUrl     WebP 포맷 URL
   */
  public static ImageResponse toImageResponse(
      Image image,
      String originalUrl,
      String jpgUrl,
      String webpUrl
  ) {
    return new ImageResponse(
        image.getImageId(),
        image.getImageType(),
        image.getStoredFilename(),
        image.getOriginalExtension(),
        originalUrl,
        jpgUrl,
        webpUrl,
        image.getCreatedAt()
    );
  }

  /**
   * Image Entity → ImageUploadResponse DTO 변환
   */
  public static ImageUploadResponse toImageUploadResponse(
      Image image,
      String originalUrl,
      String jpgUrl,
      String webpUrl
  ) {
    return new ImageUploadResponse(
        image.getImageId(),
        image.getImageType(),
        image.getStoredFilename(),
        image.getOriginalExtension(),
        originalUrl,
        jpgUrl,
        webpUrl
    );
  }
}
