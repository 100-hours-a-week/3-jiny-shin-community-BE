package com.jinyshin.ktbcommunity.global.constants;

public final class ApiMessages {

  private ApiMessages() {
  }

  // Auth
  public static final String LOGIN_SUCCESS = "login_success";
  public static final String LOGOUT_SUCCESS = "logout_success";

  // User
  public static final String SIGNUP_SUCCESS = "signup_success";
  public static final String USER_RETRIEVED = "user_retrieved";
  public static final String PROFILE_UPDATED = "profile_updated";
  public static final String PASSWORD_UPDATED = "password_updated";
  public static final String EMAIL_CHECKED = "email_checked";
  public static final String NICKNAME_CHECKED = "nickname_checked";
  public static final String PROFILE_IMAGE_DELETED = "profile_image_deleted";

  // Post
  public static final String POSTS_RETRIEVED = "posts_retrieved";
  public static final String MY_POSTS_RETRIEVED = "my_posts_retrieved";
  public static final String POST_RETRIEVED = "post_retrieved";
  public static final String POST_CREATED = "post_created";
  public static final String POST_UPDATED = "post_updated";
  public static final String POST_DELETED = "post_deleted";

  // Comment
  public static final String COMMENTS_RETRIEVED = "comments_retrieved";
  public static final String COMMENT_CREATED = "comment_created";
  public static final String COMMENT_UPDATED = "comment_updated";
  public static final String COMMENT_DELETED = "comment_deleted";

  // Like
  public static final String LIKE_ADDED = "like_added";
  public static final String LIKE_REMOVED = "like_removed";

  // Image
  public static final String IMAGE_UPLOADED = "이미지 업로드 성공";
  public static final String IMAGE_METADATA_SAVED = "이미지 메타정보 저장 성공";
  public static final String IMAGE_RETRIEVED = "이미지 조회 성공";
  public static final String IMAGES_RETRIEVED = "이미지 목록 조회 성공";
  public static final String IMAGE_DELETED = "이미지 삭제 성공";

  // AI Generation
  public static final String AI_GENERATION_REMAINING = "ai_generation_remaining";

  // Admin
  public static final String ADMIN_POST_DELETED = "admin_post_deleted";
  public static final String ADMIN_COMMENT_DELETED = "admin_comment_deleted";

  // Password
  public static final String PASSWORD_VERIFIED = "비밀번호가 일치합니다.";
  public static final String PASSWORD_NOT_MATCHED = "비밀번호가 일치하지 않습니다.";
}
