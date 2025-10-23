package com.jinyshin.ktbcommunity.domain.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.service.UserService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import com.jinyshin.ktbcommunity.global.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    return new ResponseEntity<>(
        ApiResponse.success("signup_success", response),
        CREATED
    );
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    UserInfoResponse response = userService.getUser(principal.getUserId());
    return new ResponseEntity<>(ApiResponse.success("user_retrieved", response), OK);
  }

  @PatchMapping("/me")
  public ResponseEntity<ApiResponse<UpdatedProfileResponse>> updateProfile(
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @Valid @RequestBody ProfileUpdateRequest request) {
    UpdatedProfileResponse response = userService.updateProfile(principal.getUserId(), request);
    return new ResponseEntity<>(ApiResponse.success("profile_updated", response), OK);
  }

  @PatchMapping("/me/password")
  public ResponseEntity<ApiResponse<Void>> updatePassword(
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @Valid @RequestBody PasswordUpdateRequest request) {
    userService.updatePassword(principal.getUserId(), request);
    return new ResponseEntity<>(ApiResponse.success("password_updated", null), OK);
  }

  @GetMapping("/check-email")
  public ResponseEntity<ApiResponse<AvailabilityResponse>> checkEmail(@RequestParam String email) {
    AvailabilityResponse response = userService.checkEmail(email);
    return new ResponseEntity<>(
        ApiResponse.success("email_checked", response),
        OK
    );
  }

  @GetMapping("/check-nickname")
  public ResponseEntity<ApiResponse<AvailabilityResponse>> checkNickname(
      @RequestParam String nickname) {
    AvailabilityResponse response = userService.checkNickname(nickname);
    return new ResponseEntity<>(
        ApiResponse.success("nickname_checked", response),
        OK
    );
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
    userService.deleteUser(principal.getUserId());
    return new ResponseEntity<>(NO_CONTENT);
  }
}