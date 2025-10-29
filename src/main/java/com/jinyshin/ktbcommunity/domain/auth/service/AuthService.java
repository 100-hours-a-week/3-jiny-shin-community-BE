package com.jinyshin.ktbcommunity.domain.auth.service;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import com.jinyshin.ktbcommunity.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 로그인 처리: 이메일과 비밀번호 검증 후 세션에 userId 저장
   */
  public void login(LoginRequest request, HttpSession session) {
    // 사용자 조회
    User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
        .orElseThrow(ResourceNotFoundException::user);

    // 비밀번호 검증
    if (!checkPassword(user, request.password())) {
      throw UnauthorizedException.invalidCredentials();
    }

    // 세션에 userId 저장
    session.setAttribute("userId", user.getUserId());
    log.info("유저 로그인: userId={}", user.getUserId());
  }

  /**
   * 로그아웃 처리: 세션 무효화
   */
  public void logout(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    session.invalidate();
    log.info("유저 로그아웃: userId={}", userId);
  }

  private boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPasswordHash());
  }
}
