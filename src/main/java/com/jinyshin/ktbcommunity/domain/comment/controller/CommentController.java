package com.jinyshin.ktbcommunity.domain.comment.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.COMMENT_CREATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.COMMENT_DELETED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.COMMENT_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.COMMENTS_RETRIEVED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.comment.dto.request.CreateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.request.UpdateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CommentListResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CreatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.UpdatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.service.CommentService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/posts/{postId}/comments")
  @Operation(
      summary = "댓글 작성",
      description = "게시글에 댓글을 작성합니다. 인증이 필요합니다."
  )
  @ApiResponse(
      responseCode = "201",
      description = "댓글 작성 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<CreatedCommentResponse>> createComment(
      @Parameter(description = "게시글 ID")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId,
      @Parameter(description = "댓글 작성 요청 데이터")
      @Valid @RequestBody CreateCommentRequest request) {
    CreatedCommentResponse response = commentService.createComment(postId, userId, request);
    return ResponseEntity.status(CREATED).body(BaseResponse.success(COMMENT_CREATED, response));
  }

  @GetMapping("/posts/{postId}/comments")
  @Operation(
      summary = "댓글 목록 조회",
      description = "게시글의 댓글 목록을 커서 기반 페이지네이션으로 조회합니다. 인증은 선택사항입니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "댓글 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<CommentListResponse>> getComments(
      @Parameter(description = "게시글 ID")
      @PathVariable Long postId,
      @Parameter(description = "커서 (다음 페이지를 위한 마지막 댓글 ID)")
      @RequestParam(required = false) Long cursor,
      @Parameter(description = "정렬 방식 (asc: 오래된순, desc: 최신순)")
      @RequestParam(defaultValue = "desc") String sort,
      @Parameter(description = "페이지당 댓글 수 (기본값: 10)")
      @RequestParam(defaultValue = "10") int limit,
      @Parameter(hidden = true)
      @RequestAttribute(required = false) Long userId) {
    CommentListResponse response = commentService.getComments(postId, cursor, sort, limit, userId);
    return ResponseEntity.ok(BaseResponse.success(COMMENTS_RETRIEVED, response));
  }

  @PatchMapping("/comments/{commentId}")
  @Operation(
      summary = "댓글 수정",
      description = "자신이 작성한 댓글을 수정합니다. 인증이 필요합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "댓글 수정 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "권한 없음 (자신의 댓글이 아님)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "댓글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<UpdatedCommentResponse>> updateComment(
      @Parameter(description = "댓글 ID")
      @PathVariable Long commentId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId,
      @Parameter(description = "댓글 수정 요청 데이터")
      @Valid @RequestBody UpdateCommentRequest request) {
    UpdatedCommentResponse response = commentService.updateComment(commentId, userId, request);
    return ResponseEntity.ok(BaseResponse.success(COMMENT_UPDATED, response));
  }

  @DeleteMapping("/comments/{commentId}")
  @Operation(
      summary = "댓글 삭제",
      description = "자신이 작성한 댓글을 삭제합니다. 인증이 필요합니다."
  )
  @ApiResponse(
      responseCode = "204",
      description = "댓글 삭제 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "권한 없음 (자신의 댓글이 아님)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "댓글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<Void>> deleteComment(
      @Parameter(description = "댓글 ID")
      @PathVariable Long commentId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    commentService.deleteComment(commentId, userId);
    return ResponseEntity.status(NO_CONTENT).body(BaseResponse.success(COMMENT_DELETED, null));
  }
}
