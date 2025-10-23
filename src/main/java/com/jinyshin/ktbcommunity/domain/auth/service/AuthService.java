package com.jinyshin.ktbcommunity.domain.auth.service;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  public UserInfoResponse login(LoginRequest request) {
    // 인증 수행
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    // SecurityContext에 인증 정보 설정 (세션에 저장)
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 사용자 정보 조회 및 반환
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(ResourceNotFoundException::user);

    return new UserInfoResponse(
        user.getUserId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImage() != null ? user.getProfileImage().getS3Key() : null
    );
  }

  public void logout(HttpServletRequest request) {
    
    SecurityContextHolder.clearContext();

    // 세션 무효화
    if (request.getSession(false) != null) {
      request.getSession().invalidate();
    }
  }
}
