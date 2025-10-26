package com.jinyshin.ktbcommunity.domain.auth.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGIN_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGOUT_SUCCESS;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.auth.service.AuthService;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
  public ResponseEntity<ApiResponse<UserInfoResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    UserInfoResponse response = authService.login(request);
    return new ResponseEntity<>(
        ApiResponse.success("login_success", response),
        OK
    );
    return ResponseEntity.ok(ApiResponse.success(LOGIN_SUCCESS, response));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    authService.logout(request);

    return new ResponseEntity<>(
        ApiResponse.success("logout_success", null),
        OK
    );
    return ResponseEntity.ok(ApiResponse.success(LOGOUT_SUCCESS, null));
  }
}

