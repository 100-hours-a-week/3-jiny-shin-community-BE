package com.jinyshin.ktbcommunity.domain.image.dto;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.util.ImageUrlGenerator;
import com.jinyshin.ktbcommunity.domain.image.util.ImageUrls;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMapper {

  private final ImageUrlGenerator urlGenerator;

  public ImageResponse toImageResponse(Image image) {
    return toImageResponse(image, image.getCreatedAt());
  }

  public ImageResponse toImageResponse(
      Image image,
      LocalDateTime createdAt
  ) {
    return toImageResponse(image, false, createdAt);
  }

  public ImageResponse toImageResponse(
      Image image,
      boolean useThumbnail
  ) {
    return toImageResponse(image, useThumbnail, image.getCreatedAt());
  }

  public ImageResponse toImageResponse(
      Image image,
      boolean useThumbnail,
      LocalDateTime createdAt
  ) {
    ImageUrls urls = urlGenerator.generateUrls(image, useThumbnail);

    return new ImageResponse(
        image.getImageId(),
        image.getImageType(),
        image.getStoredFilename(),
        image.getOriginalExtension(),
        urls.jpgUrl(),
        urls.webpUrl(),
        createdAt
    );
  }
}
