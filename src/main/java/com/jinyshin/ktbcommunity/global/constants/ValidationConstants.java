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

  // Messages
  public static final String DELETED_COMMENT_MESSAGE = "삭제된 댓글입니다";
}