package com.jinyshin.ktbcommunity.global.constants;

public final class JwtConstants {

  private JwtConstants() {
    throw new AssertionError("Cannot instantiate constants class");
  }

  // 토큰 만료 시간 (초)
  public static final int ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15분
  public static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 3600; // 14일

  // HTTP 헤더
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final int BEARER_PREFIX_LENGTH = 7;
}