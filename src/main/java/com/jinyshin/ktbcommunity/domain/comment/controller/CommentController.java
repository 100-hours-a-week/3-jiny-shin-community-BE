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
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import com.jinyshin.ktbcommunity.global.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/posts/{postId}/comments")
  public ResponseEntity<ApiResponse<CreatedCommentResponse>> createComment(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @Valid @RequestBody CreateCommentRequest request) {
    CreatedCommentResponse response = commentService.createComment(postId, principal.getUserId(),
        request);
    return ResponseEntity.status(CREATED).body(ApiResponse.success(COMMENT_CREATED, response));
  }

  @GetMapping("/posts/{postId}/comments")
  public ResponseEntity<ApiResponse<CommentListResponse>> getComments(
      @PathVariable Long postId,
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false, defaultValue = "desc") String sort,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserPrincipal principal) {
    Long currentUserId = principal != null ? principal.getUserId() : null;
    CommentListResponse response = commentService.getComments(postId, cursor, sort, limit,
        currentUserId);
    return ResponseEntity.ok(ApiResponse.success(COMMENTS_RETRIEVED, response));
  }

  @PatchMapping("/comments/{commentId}")
  public ResponseEntity<ApiResponse<UpdatedCommentResponse>> updateComment(
      @PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @Valid @RequestBody UpdateCommentRequest request) {
    UpdatedCommentResponse response = commentService.updateComment(commentId,
        principal.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.success(COMMENT_UPDATED, response));
  }

  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<ApiResponse<Void>> deleteComment(
      @PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserPrincipal principal) {
    commentService.deleteComment(commentId, principal.getUserId());
    return ResponseEntity.status(NO_CONTENT).body(ApiResponse.success(COMMENT_DELETED, null));
  }
}
