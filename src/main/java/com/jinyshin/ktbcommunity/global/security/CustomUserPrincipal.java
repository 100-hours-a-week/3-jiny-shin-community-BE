package com.jinyshin.ktbcommunity.global.security;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security의 UserDetails를 구현한 커스텀 사용자 정보 클래스
 *
 * <p>인증된 사용자의 정보를 SecurityContext에 저장하고, Controller에서 @AuthenticationPrincipal을 통해
 * 바로 접근할 수 있게 합니다.
 *
 * <p>주요 개선 사항:
 * <ul>
 *   <li>userId를 포함하여 매 요청마다 DB 조회 불필요</li>
 *   <li>nickname, profileImage 등 추가 정보 포함 가능</li>
 *   <li>Controller에서 Repository 의존성 제거</li>
 * </ul>
 */
@Getter
@Builder
public class CustomUserPrincipal implements UserDetails {

  private final Long userId;
  private final String email;
  private final String password;
  private final String nickname;
  private final String profileImage;
  private final Collection<? extends GrantedAuthority> authorities;

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * User 엔티티로부터 CustomUserPrincipal 생성
   *
   * @param user User 엔티티
   * @return CustomUserPrincipal 인스턴스
   */
  public static CustomUserPrincipal from(User user) {
    return CustomUserPrincipal.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .password(user.getPasswordHash())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage() != null ? user.getProfileImage().getS3Key() : null)
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
        .build();
  }
}