package com.jinyshin.ktbcommunity.domain.image.entity;

import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
  PROFILE("프로필 이미지", 0.3f, "profile"),
  POST_CONTENTS("게시글 본문", 0.7f, "post_content"),
  POST_THUMBNAIL("게시글 썸네일", 0.3f, "post_thumbnail");

  private final String description;
  private final float compressionQuality;
  private final String fileNameSuffix;

  /**
   * 파일명에서 ImageType 추출
   *
   * @param filename 파일명
   * @return ImageType
   * @throws ApiException 파일명 형식이 올바르지 않은 경우
   */
  public static ImageType fromFilename(String filename) {
    // 확장자 제거
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1) {
      throw new ApiException(ApiErrorCode.IMAGE_INVALID_FORMAT);
    }
    String nameWithoutExt = filename.substring(0, lastDotIndex);

    // endsWith로 매칭
    for (ImageType type : ImageType.values()) {
      String suffix = "_" + type.getFileNameSuffix();
      if (nameWithoutExt.endsWith(suffix)) {
        return type;
      }
    }
    throw new ApiException(ApiErrorCode.IMAGE_INVALID_FORMAT);
  }
}