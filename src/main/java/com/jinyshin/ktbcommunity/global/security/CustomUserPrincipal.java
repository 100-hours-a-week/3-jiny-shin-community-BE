package com.jinyshin.ktbcommunity.global.security;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class CustomUserPrincipal implements UserDetails {

  private final Long userId;
  private final String email;
  private final String password;
  private final String nickname;
  private final String profileImage;
  private final Collection<? extends GrantedAuthority> authorities;

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
}