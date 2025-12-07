package com.jinyshin.ktbcommunity.domain.user.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.EMAIL_CHECKED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.NICKNAME_CHECKED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PASSWORD_NOT_MATCHED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PASSWORD_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PASSWORD_VERIFIED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PROFILE_IMAGE_DELETED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.PROFILE_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.SIGNUP_SUCCESS;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.USER_RETRIEVED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.jinyshin.ktbcommunity.domain.user.dto.request.PasswordUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.VerifyPasswordRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.AvailabilityResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.VerifyPasswordResponse;
import com.jinyshin.ktbcommunity.domain.user.service.UserService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

  private final UserService userService;

  @PostMapping
  @Operation(
      summary = "회원가입",
      description = "새로운 사용자를 등록합니다. 이메일과 닉네임은 중복될 수 없습니다."
  )
  @ApiResponse(
      responseCode = "201",
      description = "회원가입 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값 (이메일/닉네임 중복 포함)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<UserInfoResponse>> signup(
      @Parameter(description = "회원가입 요청 데이터")
      @Valid @RequestBody SignupRequest request,
      HttpSession session) {
    // 기존 세션 무효화 (세션 고정 공격 방지)
    session.invalidate();

    UserInfoResponse response = userService.signup(request);
    return ResponseEntity.status(CREATED).body(BaseResponse.success(SIGNUP_SUCCESS, response));
  }

  @GetMapping("/me")
  @Operation(
      summary = "내 정보 조회",
      description = "현재 로그인한 사용자의 정보를 조회합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "사용자를 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<UserInfoResponse>> getMyInfo(
      @Parameter(hidden = true) @RequestAttribute Long userId) {
    UserInfoResponse response = userService.getUser(userId);
    return ResponseEntity.ok(BaseResponse.success(USER_RETRIEVED, response));
  }

  @PatchMapping("/me")
  @Operation(
      summary = "프로필 수정",
      description = "현재 로그인한 사용자의 프로필(닉네임, 프로필 이미지)을 수정합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "수정 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값 (닉네임 중복 포함)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "사용자를 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<UpdatedProfileResponse>> updateProfile(
      @Parameter(hidden = true) @RequestAttribute Long userId,
      @Parameter(description = "프로필 수정 요청 데이터")
      @Valid @RequestBody ProfileUpdateRequest request) {
    UpdatedProfileResponse response = userService.updateProfile(userId, request);
    return ResponseEntity.ok(BaseResponse.success(PROFILE_UPDATED, response));
  }

  @PatchMapping("/me/password")
  @Operation(
      summary = "비밀번호 변경",
      description = "현재 로그인한 사용자의 비밀번호를 변경합니다. 현재 비밀번호 확인이 필요합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "변경 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값 (현재 비밀번호 불일치 포함)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "사용자를 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> updatePassword(
      @Parameter(hidden = true) @RequestAttribute Long userId,
      @Parameter(description = "비밀번호 변경 요청 데이터")
      @Valid @RequestBody PasswordUpdateRequest request) {
    userService.updatePassword(userId, request);
    return ResponseEntity.ok(BaseResponse.success(PASSWORD_UPDATED, null));
  }

  @PostMapping("/me/verify-password")
  @Operation(
      summary = "비밀번호 확인",
      description = "현재 비밀번호 일치 여부를 확인합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "비밀번호가 일치",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "비밀번호 불일치",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<VerifyPasswordResponse>> verifyPassword(
      @Parameter(hidden = true) @RequestAttribute Long userId,
      @Parameter(description = "비밀번호 확인 요청 데이터")
      @Valid @RequestBody VerifyPasswordRequest request) {
    VerifyPasswordResponse response = userService.verifyPassword(userId, request.password());

    if (response.valid()) {
      return ResponseEntity.ok(BaseResponse.success(PASSWORD_VERIFIED, response));
    }

    return ResponseEntity.status(BAD_REQUEST)
        .body(BaseResponse.success(PASSWORD_NOT_MATCHED, response));
  }

  @GetMapping("/check-email")
  @Operation(
      summary = "이메일 중복 확인",
      description = "회원가입 시 이메일 중복 여부를 확인합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "확인 완료",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 이메일 형식",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<AvailabilityResponse>> checkEmail(
      @Parameter(description = "확인할 이메일 주소", example = "user@example.com")
      @RequestParam String email) {
    AvailabilityResponse response = userService.checkEmail(email);
    return ResponseEntity.ok(BaseResponse.success(EMAIL_CHECKED, response));
  }

  @GetMapping("/check-nickname")
  @Operation(
      summary = "닉네임 중복 확인",
      description = "회원가입 또는 프로필 수정 시 닉네임 중복 여부를 확인합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "확인 완료",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 닉네임 형식",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<AvailabilityResponse>> checkNickname(
      @Parameter(description = "확인할 닉네임", example = "홍길동")
      @RequestParam String nickname) {
    AvailabilityResponse response = userService.checkNickname(nickname);
    return ResponseEntity.ok(BaseResponse.success(NICKNAME_CHECKED, response));
  }

  @DeleteMapping("/me/profile-image")
  @Operation(
      summary = "프로필 이미지 삭제",
      description = "현재 로그인한 사용자의 프로필 이미지를 삭제합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "삭제 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "사용자를 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> deleteProfileImage(
      @Parameter(hidden = true) @RequestAttribute Long userId) {
    userService.deleteProfileImage(userId);
    return ResponseEntity.ok(BaseResponse.success(PROFILE_IMAGE_DELETED, null));
  }

  @DeleteMapping("/me")
  @Operation(
      summary = "회원 탈퇴",
      description = "현재 로그인한 사용자의 계정을 삭제합니다. 세션도 함께 무효화됩니다."
  )
  @ApiResponse(
      responseCode = "204",
      description = "탈퇴 성공 (No Content)"
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "사용자를 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<Void> deleteUser(
      @Parameter(hidden = true) @RequestAttribute Long userId,
      HttpSession session) {
    userService.deleteUser(userId);
    session.invalidate();
    return ResponseEntity.status(NO_CONTENT).build();
  }
}
