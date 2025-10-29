package com.jinyshin.ktbcommunity.domain.auth.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGIN_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGOUT_SUCCESS;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.auth.service.AuthService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Void>> login(
      @Valid @RequestBody LoginRequest request,
      HttpSession session) {
    authService.login(request, session);
    // JSESSIONID 쿠키가 자동으로 Set-Cookie 헤더에 포함됨
    return ResponseEntity.ok(ApiResponse.success(LOGIN_SUCCESS, null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
    authService.logout(session);
    return ResponseEntity.ok(ApiResponse.success(LOGOUT_SUCCESS, null));
  }
}
