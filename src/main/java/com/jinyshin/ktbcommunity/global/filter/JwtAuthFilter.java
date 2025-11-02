package com.jinyshin.ktbcommunity.global.filter;

import static com.jinyshin.ktbcommunity.global.constants.JwtConstants.AUTHORIZATION_HEADER;
import static com.jinyshin.ktbcommunity.global.constants.JwtConstants.BEARER_PREFIX;
import static com.jinyshin.ktbcommunity.global.constants.JwtConstants.BEARER_PREFIX_LENGTH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import com.jinyshin.ktbcommunity.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final String[] EXCLUDED_PATHS = {
      "/auth/login",
      "/users/check-email",
      "/users/check-nickname"
  };

  private final JwtProvider jwtProvider;
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

    log.debug("[JwtAuthFilter] {} {} - Filter 제외 여부: {}",
        method, uri, shouldExclude);
    return shouldExclude;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String uri = request.getRequestURI();
    String method = request.getMethod();
    log.debug("[JwtAuthFilter] 인증 체크 시작: {} {}", method, uri);

    // 비회원 접근 가능한 엔드포인트
    boolean isPublicReadEndpoint = "GET".equals(method) &&
        (uri.equals("/posts") || uri.startsWith("/posts/"));

    Optional<String> token = extractToken(request);

    if (isPublicReadEndpoint) {
      // 비회원 조회: 토큰이 있으면 userId 저장, 없으면 그냥 통과
      if (token.isPresent()) {
        if (validateAndSetAttributes(token.get(), request)) {
          Long userId = (Long) request.getAttribute("userId");
          log.debug("[JwtAuthFilter] 회원 조회: userId={}, uri={}", userId, uri);
        } else {
          log.debug("[JwtAuthFilter] 비회원 조회 (토큰 검증 실패): uri={}", uri);
        }
      } else {
        log.debug("[JwtAuthFilter] 비회원 조회: uri={}", uri);
      }
      filterChain.doFilter(request, response);
      return;
    }

    // 나머지 엔드포인트는 인증 필수
    if (token.isEmpty()) {
      log.warn("[JwtAuthFilter] 토큰 없음 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized_access");
      return;
    }

    if (!validateAndSetAttributes(token.get(), request)) {
      log.warn("[JwtAuthFilter] 토큰 검증 실패 - 401 반환: {}", uri);
      sendApiErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized_access");
      return;
    }

    Long userId = (Long) request.getAttribute("userId");
    log.debug("[JwtAuthFilter] 인증 성공: userId={}, uri={}", userId, uri);
    filterChain.doFilter(request, response);
  }

  private Optional<String> extractToken(HttpServletRequest request) {
    String header = request.getHeader(AUTHORIZATION_HEADER);

    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      return Optional.empty();
    }

    return Optional.of(header.substring(BEARER_PREFIX_LENGTH));
  }

  private boolean validateAndSetAttributes(String token, HttpServletRequest request) {
    try {
      Jws<Claims> jws = jwtProvider.parse(token);
      Claims body = jws.getBody();
      request.setAttribute("userId", Long.valueOf(body.getSubject()));
      request.setAttribute("role", body.get("role"));
      return true;
    } catch (Exception e) {
      log.debug("[JwtAuthFilter] JWT 파싱 실패: {}", e.getMessage());
      return false;
    }
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