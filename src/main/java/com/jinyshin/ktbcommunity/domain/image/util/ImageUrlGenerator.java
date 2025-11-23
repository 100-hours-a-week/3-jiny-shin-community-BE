package com.jinyshin.ktbcommunity.domain.image.util;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 이미지 URL 생성 전담 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class ImageUrlGenerator {

  private final FileService fileService;

  /**
   * 고품질 이미지 URL 생성
   */
  public ImageUrls generateUrls(Image image) {
    return generateUrls(image, false);
  }

  /**
   * 이미지 URL 생성
   *
   * @param image       Image 엔티티
   * @param useThumbnail 썸네일 사용 여부 (true: 썸네일, false: 고품질)
   */
  public ImageUrls generateUrls(Image image, boolean useThumbnail) {
    String jpgUrl = generateJpgUrl(image, useThumbnail);
    String webpUrl = generateWebpUrl(image, useThumbnail);

    return new ImageUrls(jpgUrl, webpUrl);
  }

  /**
   * JPG URL 생성 (썸네일/고품질 선택)
   */
  private String generateJpgUrl(Image image, boolean useThumbnail) {
    String storedFilename = image.getStoredFilename();

    if (useThumbnail) {
      String thumbFilename = storedFilename + "_thumb.jpg";
      if (fileService.existsFile(thumbFilename)) {
        return fileService.getPublicUrl(thumbFilename);
      }
    }

    return fileService.getPublicUrl(storedFilename + ".jpg");
  }

  /**
   * WebP URL 생성 (썸네일/고품질 선택)
   */
  private String generateWebpUrl(Image image, boolean useThumbnail) {
    String storedFilename = image.getStoredFilename();

    if (useThumbnail) {
      String thumbFilename = storedFilename + "_thumb.webp";
      if (fileService.existsFile(thumbFilename)) {
        return fileService.getPublicUrl(thumbFilename);
      }
    }

    return fileService.getPublicUrl(storedFilename + ".webp");
  }
}