package com.jinyshin.ktbcommunity.domain.user.service;

import com.jinyshin.ktbcommunity.domain.user.dto.UserMapper;
import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.BadRequestException;
import com.jinyshin.ktbcommunity.global.exception.ConflictException;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserInfoResponse signup(SignupRequest signUpRequest) {
    // 이메일 중복 체크
    if (!checkEmail(signUpRequest.email()).available()) {
      throw ConflictException.emailAlreadyExists();
    }

    // 닉네임 중복 체크
    if (!checkNickname(signUpRequest.nickname()).available()) {
      throw ConflictException.nicknameAlreadyExists();
    }

    // 비밀번호 해싱
    String hashedPassword = passwordEncoder.encode(signUpRequest.password());

    // User 엔티티 생성
    User user = new User(
        signUpRequest.email(),
        signUpRequest.nickname(),
        hashedPassword
    );

    // TODO: 프로필 이미지 로직 추가

    // 저장
    User savedUser = userRepository.save(user);

    // Response 반환
    return UserMapper.toUserInfo(savedUser);
  }

  @Override
  public UserInfoResponse getUser(Long userId) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);
    return UserMapper.toUserInfo(user);
  }

  @Override
  @Transactional
  public UpdatedProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);

    String newNickname = user.getNickname();

    // 닉네임 변경 시 중복 체크
    if (request.nickname() != null && !request.nickname().equals(user.getNickname())) {
      if (!checkNickname(request.nickname()).available()) {
        throw ConflictException.nicknameAlreadyExists();
      }
      newNickname = request.nickname();
    }

    // TODO: 프로필 이미지 로직 추가

    // 프로필 업데이트
    user.updateProfile(newNickname, null);  // TODO: Image 추가

    // Response 반환
    return UserMapper.toUpdatedProfile(user);
  }

  @Override
  @Transactional
  public void updatePassword(Long userId, PasswordUpdateRequest request) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);

    // 현재 비밀번호 검증
    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw ForbiddenException.invalidCurrentPassword();
    }

    // 새 비밀번호가 현재 비밀번호와 같은지 검증
    if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
      throw BadRequestException.sameAsCurrentPassword();
    }

    // 새 비밀번호 해싱
    String newHashedPassword = passwordEncoder.encode(request.newPassword());

    // 비밀번호 업데이트
    user.updatePassword(newHashedPassword);
  }

  @Override
  public AvailabilityResponse checkEmail(String email) {
    return new AvailabilityResponse(!userRepository.existsByEmail(email));
  }

  @Override
  public AvailabilityResponse checkNickname(String nickname) {
    return new AvailabilityResponse(!userRepository.existsByNickname(nickname));
  }

  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);
    userRepository.delete(user);
  }
}
