package com.jinyshin.ktbcommunity.domain.user.service;

import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.VerifyPasswordResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  UserInfoResponse getUser(Long userId);

  UserInfoResponse signup(SignupRequest signUpRequest);

  UpdatedProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);

  void updatePassword(Long userId, PasswordUpdateRequest passwordUpdateRequest);

  VerifyPasswordResponse verifyPassword(Long userId, String password);

  AvailabilityResponse checkEmail(String email);

  AvailabilityResponse checkNickname(String nickname);

  void deleteUser(Long userId);

  void deleteProfileImage(Long userId);
}
