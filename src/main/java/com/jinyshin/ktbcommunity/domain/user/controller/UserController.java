package com.jinyshin.ktbcommunity.domain.user.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.EMAIL_CHECKED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.NICKNAME_CHECKED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PASSWORD_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PROFILE_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.SIGNUP_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.USER_RETRIEVED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.service.UserService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserInfoResponse>> signup(
      @Valid @RequestBody SignupRequest request) {
    UserInfoResponse response = userService.signup(request);
    return ResponseEntity.status(CREATED).body(ApiResponse.success(SIGNUP_SUCCESS, response));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    UserInfoResponse response = userService.getUser(userId);
    return ResponseEntity.ok(ApiResponse.success(USER_RETRIEVED, response));
  }

  @PatchMapping("/me")
  public ResponseEntity<ApiResponse<UpdatedProfileResponse>> updateProfile(
      HttpSession session,
      @Valid @RequestBody ProfileUpdateRequest request) {
    Long userId = (Long) session.getAttribute("userId");
    UpdatedProfileResponse response = userService.updateProfile(userId, request);
    return ResponseEntity.ok(ApiResponse.success(PROFILE_UPDATED, response));
  }

  @PatchMapping("/me/password")
  public ResponseEntity<ApiResponse<Void>> updatePassword(
      HttpSession session,
      @Valid @RequestBody PasswordUpdateRequest request) {
    Long userId = (Long) session.getAttribute("userId");
    userService.updatePassword(userId, request);
    return ResponseEntity.ok(ApiResponse.success(PASSWORD_UPDATED, null));
  }

  @GetMapping("/check-email")
  public ResponseEntity<ApiResponse<AvailabilityResponse>> checkEmail(@RequestParam String email) {
    AvailabilityResponse response = userService.checkEmail(email);
    return ResponseEntity.ok(ApiResponse.success(EMAIL_CHECKED, response));
  }

  @GetMapping("/check-nickname")
  public ResponseEntity<ApiResponse<AvailabilityResponse>> checkNickname(
      @RequestParam String nickname) {
    AvailabilityResponse response = userService.checkNickname(nickname);
    return ResponseEntity.ok(ApiResponse.success(NICKNAME_CHECKED, response));
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteUser(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    userService.deleteUser(userId);
    session.invalidate();
    return ResponseEntity.status(NO_CONTENT).build();
  }
}