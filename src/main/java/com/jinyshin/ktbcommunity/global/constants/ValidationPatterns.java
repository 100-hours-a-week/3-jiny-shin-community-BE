package com.jinyshin.ktbcommunity.global.constants;

public final class ValidationPatterns {

  private ValidationPatterns() {
  }

  public static final String PASSWORD_PATTERN =
      "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]|\\\\:;\"'<>,.?/]).{8,20}$";

  public static final String PASSWORD_MESSAGE =
      "비밀번호는 8~20자의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.";

  public static final String NICKNAME_PATTERN = "^\\S+$";

  public static final String NICKNAME_MESSAGE = "닉네임에 띄어쓰기를 사용할 수 없습니다.";
}