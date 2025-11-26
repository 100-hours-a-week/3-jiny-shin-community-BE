package com.jinyshin.ktbcommunity.domain.image.util;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.service.S3Service;
import com.jinyshin.ktbcommunity.global.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlGenerator {

  private final S3Service s3Service;
  private final S3Properties s3Properties;

  /**
   * 게시물 이미지 URL 생성
   *
   * @param image        Image 엔티티
   * @param useThumbnail 썸네일 사용 여부 (true: 썸네일, false: 본문)
   * @return ImageUrls (jpgUrl, webpUrl)
   */
  public ImageUrlsResponse generatePostUrls(Image image, boolean useThumbnail) {
    String s3Path = image.getS3Path();
    String storedFilename = image.getStoredFilename();
    String baseKey = s3Path + "/" + storedFilename;

    if (useThumbnail) {
      // 썸네일 파일 확인 → 없으면 본문 이미지로 fallback
      String thumbJpgKey = baseKey + "_thumb.jpg";
      String thumbWebpKey = baseKey + "_thumb.webp";

      if (s3Service.imageExists(thumbJpgKey)) {
        return new ImageUrlsResponse(
            buildS3Url(thumbJpgKey),
            buildS3Url(thumbWebpKey)
        );
      }
    }

    // 본문 이미지 반환
    return new ImageUrlsResponse(
        buildS3Url(baseKey + ".jpg"),
        buildS3Url(baseKey + ".webp")
    );
  }

  /**
   * 프로필 이미지 URL 생성 (항상 최적화이미지)
   *
   * @param image Image 엔티티
   * @return ImageUrls (jpgUrl, webpUrl)
   */
  public ImageUrlsResponse generateProfileUrls(Image image) {
    String s3Path = image.getS3Path();
    String storedFilename = image.getStoredFilename();
    String baseKey = s3Path + "/" + storedFilename;

    // 프로필은 항상 기본 파일 (최적화이미지)
    return new ImageUrlsResponse(
        buildS3Url(baseKey + ".jpg"),
        buildS3Url(baseKey + ".webp")
    );
  }

  /**
   * S3 URL 생성
   */
  private String buildS3Url(String s3Key) {
    String bucketName = s3Properties.getBucket();
    String region = s3Properties.getRegion();
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
  }
}