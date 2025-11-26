package com.jinyshin.ktbcommunity.domain.post.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LIKE_ADDED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LIKE_REMOVED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.post.dto.response.LikeResponse;
import com.jinyshin.ktbcommunity.domain.post.service.PostLikeService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "PostLike", description = "게시글 좋아요 관련 API")
public class PostLikeController {

  private final PostLikeService postLikeService;

  @PostMapping("/posts/{postId}/likes")
  @Operation(
      summary = "게시글 좋아요 추가",
      description = "특정 게시글에 좋아요를 추가합니다. 이미 좋아요를 누른 경우 예외가 발생합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "좋아요 추가 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "이미 좋아요를 누른 게시글",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<LikeResponse>> addLike(
      @Parameter(description = "게시글 ID")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    LikeResponse response = postLikeService.addLike(postId, userId);
    return ResponseEntity.ok(BaseResponse.success(LIKE_ADDED, response));
  }

  @DeleteMapping("/posts/{postId}/likes")
  @Operation(
      summary = "게시글 좋아요 취소",
      description = "특정 게시글의 좋아요를 취소합니다. 좋아요를 누르지 않은 경우 예외가 발생합니다."
  )
  @ApiResponse(
      responseCode = "204",
      description = "좋아요 취소 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "좋아요를 누르지 않은 게시글",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> removeLike(
      @Parameter(description = "게시글 ID")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    postLikeService.removeLike(postId, userId);
    return ResponseEntity.status(NO_CONTENT).body(BaseResponse.success(LIKE_REMOVED, null));
  }
}