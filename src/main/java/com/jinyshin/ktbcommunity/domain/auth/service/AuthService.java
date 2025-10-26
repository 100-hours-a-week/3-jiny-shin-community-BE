package com.jinyshin.ktbcommunity.domain.auth.service;

import com.jinyshin.ktbcommunity.domain.auth.dto.request.LoginRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.global.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;

  public UserInfoResponse login(LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    // 인증 수행
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    // SecurityContext 생성 및 인증 정보 설정
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    // SecurityContext를 세션에 저장
    securityContextRepository.saveContext(context, httpRequest, httpResponse);

    CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
    return new UserInfoResponse(
        principal.getUserId(),
        principal.getEmail(),
        principal.getNickname(),
        principal.getProfileImage()
    );
  }

  public void logout(HttpServletRequest request, HttpServletResponse response) {
    // SecurityContext 클리어
    SecurityContextHolder.clearContext();

    // SecurityContextRepository에서도 제거 (login과 일관성 유지)
    securityContextRepository.saveContext(
        SecurityContextHolder.createEmptyContext(),
        request,
        response
    );

    // 세션 무효화
    if (request.getSession(false) != null) {
      request.getSession().invalidate();
    }
  }
}
