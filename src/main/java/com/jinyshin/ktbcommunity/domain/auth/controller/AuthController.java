package com.jinyshin.ktbcommunity.domain.auth.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGIN_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGOUT_SUCCESS;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.auth.service.AuthService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Void>> login(
      @Valid @RequestBody LoginRequest request,
      HttpSession session) {
    Long loginUserId = authService.login(request);
    // 세션에 userId 저장
    session.setAttribute("userId", loginUserId);
    log.info("유저 로그인: userId={}", loginUserId);
    // JSESSIONID 쿠키가 자동으로 Set-Cookie 헤더에 포함됨
    return ResponseEntity.ok(ApiResponse.success(LOGIN_SUCCESS, null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      HttpSession session,
      HttpServletResponse response) {
    Long userId = (Long) session.getAttribute("userId");

    // 서버 세션 무효화
    session.invalidate();

    // 클라이언트 쿠키 삭제 (Max-Age=0)
    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setMaxAge(0); // 즉시 만료
    response.addCookie(cookie);

    log.info("유저 로그아웃: userId={}, JSESSIONID 쿠키 삭제", userId);
    return ResponseEntity.ok(ApiResponse.success(LOGOUT_SUCCESS, null));
  }
}