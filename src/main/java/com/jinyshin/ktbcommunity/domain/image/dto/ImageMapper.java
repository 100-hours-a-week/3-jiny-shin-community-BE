package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import java.util.List;
import java.util.function.Function;

public final class ImageMapper {

  private ImageMapper() {
  }

  /**
   * Image Entity → ImageResponse DTO 변환
   *
   * @param image    Image 엔티티
   * @param imageUrl S3 Public URL
   */
  public static ImageResponse toImageResponse(Image image, String imageUrl) {
    return new ImageResponse(
        image.getImageId(),
        image.getImageType(),
        image.getFilename(),
        imageUrl,
        image.getCreatedAt()
    );
  }

  /**
   * Image Entity 리스트 → ImageResponse DTO 리스트 변환
   */
  public static List<ImageResponse> toImageResponses(
      List<Image> images,
      Function<Image, String> urlProvider) {
    return images.stream()
        .map(image -> toImageResponse(image, urlProvider.apply(image)))
        .toList();
  }

  /**
   * Image Entity → ImageUploadResponse DTO 변환
   */
  public static ImageUploadResponse toImageUploadResponse(Image image, String imageUrl) {
    return new ImageUploadResponse(
        image.getImageId(),
        image.getImageType(),
        image.getFilename(),
        imageUrl
    );
  }
}
