package com.jinyshin.ktbcommunity.domain.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
  PROFILE("프로필 이미지"),
  POST("게시글 이미지");

  private final String description;
}