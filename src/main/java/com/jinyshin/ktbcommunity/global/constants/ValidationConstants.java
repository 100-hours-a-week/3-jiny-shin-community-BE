package com.jinyshin.ktbcommunity.global.constants;

public final class ValidationConstants {

  private ValidationConstants() {
  }

  // Post
  public static final int POST_TITLE_MIN = 2;
  public static final int POST_TITLE_MAX = 26;
  public static final int POST_CONTENT_MIN = 2;
  public static final int POST_CONTENT_MAX = 10_000;
  public static final int POST_CONTENT_PREVIEW_LENGTH = 100;

  // Comment
  public static final int COMMENT_CONTENT_MIN = 1;
  public static final int COMMENT_CONTENT_MAX = 1000;

  // Pagination
  public static final int DEFAULT_PAGE_LIMIT = 10;
  public static final int MAX_PAGE_LIMIT = 50;

  // Image
  public static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024; // 10MB
  public static final String[] SUPPORTED_IMAGE_FORMATS = {"jpg", "jpeg", "png", "webp"};
  public static final float PROFILE_IMAGE_QUALITY = 0.6f;
  public static final float POST_ORIGINAL_QUALITY = 0.2f;
  public static final float POST_THUMBNAIL_QUALITY = 0.6f;

  // Messages
  public static final String DELETED_COMMENT_MESSAGE = "삭제된 댓글입니다";
}