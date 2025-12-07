package com.jinyshin.ktbcommunity.domain.user.service;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageStatus;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.domain.image.util.ImageUrlGenerator;
import com.jinyshin.ktbcommunity.domain.user.dto.UserMapper;
import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.VerifyPasswordResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.BadRequestException;
import com.jinyshin.ktbcommunity.global.exception.ConflictException;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import java.util.UUID;
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
  private final ImageRepository imageRepository;
  private final ImageUrlGenerator imageUrlGenerator;

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

    // 프로필 이미지 검증 및 처리
    Image profileImage = null;
    if (signUpRequest.profileImageId() != null) {
      profileImage = validateAndActivateImage(signUpRequest.profileImageId());
    }

    // User 엔티티 생성
    User user = new User(
        signUpRequest.email(),
        signUpRequest.nickname(),
        hashedPassword,
        profileImage
    );

    // 저장
    User savedUser = userRepository.save(user);

    // 프로필 이미지 URL 생성
    ImageUrlsResponse profileImageUrls = savedUser.getProfileImage() != null
        ? imageUrlGenerator.generateProfileUrls(savedUser.getProfileImage())
        : null;

    // Response 반환
    return UserMapper.toUserInfo(savedUser, profileImageUrls);
  }

  @Override
  public UserInfoResponse getUser(Long userId) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);

    // 프로필 이미지 URL 생성
    ImageUrlsResponse profileImageUrls = user.getProfileImage() != null
        ? imageUrlGenerator.generateProfileUrls(user.getProfileImage())
        : null;

    return UserMapper.toUserInfo(user, profileImageUrls);
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

    // 프로필 이미지 변경 처리
    Image newProfileImage = user.getProfileImage();
    if (request.profileImageId() != null) {
      // 새 이미지 검증 및 활성화
      newProfileImage = validateAndActivateImage(request.profileImageId());

      // 기존 이미지가 있고 새 이미지와 다르면 Soft Delete
      if (user.getProfileImage() != null
          && !user.getProfileImage().getImageId().equals(newProfileImage.getImageId())) {
        user.getProfileImage().markAsDeleted();
      }
    }

    // 프로필 업데이트
    user.updateProfile(newNickname, newProfileImage);

    // 프로필 이미지 URL 생성
    ImageUrlsResponse profileImageUrls = user.getProfileImage() != null
        ? imageUrlGenerator.generateProfileUrls(user.getProfileImage())
        : null;

    // Response 반환
    return UserMapper.toUpdatedProfile(user, profileImageUrls);
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
  @Transactional(readOnly = true)
  public VerifyPasswordResponse verifyPassword(Long userId, String password) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);

    boolean valid = passwordEncoder.matches(password, user.getPasswordHash());
    return new VerifyPasswordResponse(valid);
  }

  @Override
  public AvailabilityResponse checkEmail(String email) {
    return new AvailabilityResponse(!userRepository.existsByEmailAndDeletedAtIsNull(email));
  }

  @Override
  public AvailabilityResponse checkNickname(String nickname) {
    return new AvailabilityResponse(!userRepository.existsByNicknameAndDeletedAtIsNull(nickname));
  }

  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);
    String anonymizedEmail = "deleted-" + user.getUserId() + "-" + System.currentTimeMillis()
        + "@example.com";
    String anonymizedNickname = "deleted-" + user.getUserId();
    String anonymizedPasswordHash = passwordEncoder.encode("deleted-" + UUID.randomUUID());

    user.anonymize(anonymizedEmail, anonymizedNickname, anonymizedPasswordHash);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void deleteProfileImage(Long userId) {
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(ResourceNotFoundException::user);

    if (user.getProfileImage() != null) {
      user.getProfileImage().markAsDeleted();
      user.updateProfile(user.getNickname(), null);
    }
  }

  private Image validateAndActivateImage(Long imageId) {
    // 이미지 존재 확인
    Image image = imageRepository.findById(imageId)
        .orElseThrow(ResourceNotFoundException::image);

    // TEMP 상태 확인
    if (image.getStatus() != ImageStatus.TEMP) {
      throw BadRequestException.imageAlreadyUsed();
    }

    // 1시간 만료 확인
    if (image.isExpired(1)) {
      throw BadRequestException.imageExpired();
    }

    // ACTIVE 상태로 변경
    image.markAsActive();

    return image;
  }
}
