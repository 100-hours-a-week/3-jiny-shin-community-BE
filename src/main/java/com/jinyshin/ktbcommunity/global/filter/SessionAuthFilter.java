package com.jinyshin.ktbcommunity.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

  // 인증 없이 접근 가능한 경로 (메서드 무관)
  private static final String[] PUBLIC_PATHS = {
      "/api/auth/login",
      "/api/users/check-email",
      "/api/users/check-nickname",
      "/api/feedback",
      "/actuator/health"
  };

  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String method = request.getMethod();

    // CORS preflight 요청 허용
    if ("OPTIONS".equals(method)) {
      return true;
    }

    // Swagger UI 및 API 문서 허용
    if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
      return true;
    }

    // 회원가입 API 요청(POST) 허용
    if ("/api/users".equals(uri) && "POST".equals(method)) {
      return true;
    }

    // 그 외 공개 경로
    return Arrays.asList(PUBLIC_PATHS).contains(uri);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String uri = request.getRequestURI();
    String method = request.getMethod();

    // 비회원 접근 가능한 엔드포인트 (게시글 목록 조회만)
    boolean isPublicReadEndpoint = "GET".equals(method) && uri.equals("/api/posts");

    HttpSession session = request.getSession(false);

    if (isPublicReadEndpoint) {
      // 비회원 조회: 세션이 있으면 userId 저장, 없으면 그냥 통과
      if (session != null) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
          request.setAttribute("userId", userId);
        }
      }
      filterChain.doFilter(request, response);
      return;
    }

    // 나머지 엔드포인트는 인증 필수
    if (session == null) {
      log.warn("[SessionAuthFilter] 세션 없음 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized_access");
      return;
    }

    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
      log.warn("[SessionAuthFilter] userId 없음 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized_access");
      return;
    }

    log.debug("[SessionAuthFilter] 인증 성공: userId={}, uri={}", userId, uri);
    request.setAttribute("userId", userId);
    filterChain.doFilter(request, response);
  }

  private void sendApiErrorResponse(
      HttpServletResponse response,
      int status,
      String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    BaseResponse<Void> apiResponse = BaseResponse.error(message);
    String json = objectMapper.writeValueAsString(apiResponse);
    response.getWriter().write(json);
  }
}