package com.jinyshin.ktbcommunity.domain.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
  PROFILE("프로필 이미지", 0.7f, "profile"),
  POST_CONTENTS("게시글 본문", 0.3f, "post_content"),
  POST_THUMBNAIL("게시글 썸네일", 0.7f, "post_thumbnail");

  private final String description;
  private final float compressionQuality;
  private final String fileNamePostfix;
}