package com.jinyshin.ktbcommunity.domain.auth.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGIN_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LOGOUT_SUCCESS;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.auth.service.AuthService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(
      summary = "로그인",
      description = "이메일과 비밀번호로 로그인합니다. 성공 시 세션이 생성됩니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "로그인 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 실패 (이메일 또는 비밀번호 불일치)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> login(
      @Parameter(description = "로그인 요청 데이터")
      @Valid @RequestBody LoginRequest request,
      HttpSession session) {
    Long loginUserId = authService.login(request);
    // 세션에 userId 저장
    session.setAttribute("userId", loginUserId);
    log.info("유저 로그인: userId={}", loginUserId);
    // JSESSIONID 쿠키가 자동으로 Set-Cookie 헤더에 포함됨
    return ResponseEntity.ok(BaseResponse.success(LOGIN_SUCCESS, null));
  }

  @PostMapping("/logout")
  @Operation(
      summary = "로그아웃",
      description = "현재 세션을 무효화하고 로그아웃합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "로그아웃 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> logout(
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
    return ResponseEntity.ok(BaseResponse.success(LOGOUT_SUCCESS, null));
  }
}