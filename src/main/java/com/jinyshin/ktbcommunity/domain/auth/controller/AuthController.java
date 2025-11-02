package com.jinyshin.ktbcommunity.domain.auth.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGIN_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGOUT_SUCCESS;

import com.jinyshin.ktbcommunity.domain.auth.dto.TokenResponse;
import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.auth.service.AuthService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import com.jinyshin.ktbcommunity.global.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtProvider jwtProvider;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    Long loginUserId = authService.login(request);

    // JWT 토큰 발급
    String accessToken = jwtProvider.createAccessToken(loginUserId, "USER");
    String refreshToken = jwtProvider.createRefreshToken(loginUserId);

    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

    log.info("유저 로그인: userId={}", loginUserId);
    return ResponseEntity.ok(ApiResponse.success(LOGIN_SUCCESS, tokenResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestAttribute Long userId) {
    log.info("유저 로그아웃: userId={}", userId);
    return ResponseEntity.ok(ApiResponse.success(LOGOUT_SUCCESS, null));
  }
}