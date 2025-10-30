package com.jinyshin.ktbcommunity.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
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

  private static final String[] EXCLUDED_PATHS = {
      "/auth/login",
      "/users/check-email",
      "/users/check-nickname"
  };

  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String method = request.getMethod();

    boolean shouldExclude;
    if ("/users".equals(uri) && "POST".equals(method)) {
      shouldExclude = true;
    } else {
      shouldExclude = Arrays.asList(EXCLUDED_PATHS).contains(uri);
    }

    log.debug("[SessionAuthFilter] {} {} - Filter 제외 여부: {}",
        method, uri, shouldExclude);
    return shouldExclude;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String uri = request.getRequestURI();
    log.debug("[SessionAuthFilter] 인증 체크 시작: {}", uri);

    HttpSession session = request.getSession(false);
    if (session == null) {
      log.warn("[SessionAuthFilter] 세션 없음 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
      return;
    }

    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
      log.warn("[SessionAuthFilter] userId 없음 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
      return;
    }

    log.debug("[SessionAuthFilter] 인증 성공: userId={}, uri={}", userId, uri);
    filterChain.doFilter(request, response);
  }

  private void sendApiErrorResponse(
      HttpServletResponse response,
      int status,
      String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    ApiResponse<Void> apiResponse = ApiResponse.error(message);
    String json = objectMapper.writeValueAsString(apiResponse);
    response.getWriter().write(json);
  }

}