package com.jinyshin.ktbcommunity.domain.auth.service;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import com.jinyshin.ktbcommunity.global.exception.UnauthorizedException;
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
  public Long login(LoginRequest request) {
    // 사용자 조회
    User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
        .orElseThrow(ResourceNotFoundException::user);

    // 비밀번호 검증
    if (!checkPassword(user, request.password())) {
      throw UnauthorizedException.invalidCredentials();
    }

    return user.getUserId();
  }

  private boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPasswordHash());
  }
}
