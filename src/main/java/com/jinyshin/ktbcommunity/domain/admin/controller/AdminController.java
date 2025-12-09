package com.jinyshin.ktbcommunity.domain.admin.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.ADMIN_COMMENT_DELETED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.ADMIN_POST_DELETED;
import static org.springframework.http.HttpStatus.OK;

import com.jinyshin.ktbcommunity.domain.admin.service.AdminService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {

  private final AdminService adminService;

  @DeleteMapping("/posts/{postId}")
  @Operation(
      summary = "게시글 강제 삭제 (관리자)",
      description = "관리자 권한으로 게시글을 강제 삭제합니다. 첨부된 이미지도 함께 삭제됩니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "게시글 삭제 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "관리자 권한 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> deletePost(
      @Parameter(description = "게시글 ID", example = "1")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    adminService.deletePostByAdmin(postId, userId);
    return ResponseEntity.status(OK).body(BaseResponse.success(ADMIN_POST_DELETED, null));
  }

  @DeleteMapping("/comments/{commentId}")
  @Operation(
      summary = "댓글 강제 삭제 (관리자)",
      description = "관리자 권한으로 댓글을 강제 삭제합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "댓글 삭제 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "관리자 권한 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "댓글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> deleteComment(
      @Parameter(description = "댓글 ID", example = "1")
      @PathVariable Long commentId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    adminService.deleteCommentByAdmin(commentId, userId);
    return ResponseEntity.status(OK).body(BaseResponse.success(ADMIN_COMMENT_DELETED, null));
  }
}