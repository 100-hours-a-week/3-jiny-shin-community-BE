package com.jinyshin.ktbcommunity.domain.image.entity;

public enum ImageStatus {
  TEMP,    // 업로드 완료, 아직 User/Post에 연결 안됨
  ACTIVE,  // User/Post에 연결됨
  DELETED  // Soft Delete됨 (삭제 예정)
}