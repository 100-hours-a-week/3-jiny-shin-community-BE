package com.jinyshin.ktbcommunity.domain.post.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LIKE_ADDED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.LIKE_REMOVED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.post.dto.response.LikeResponse;
import com.jinyshin.ktbcommunity.domain.post.service.PostLikeService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

  private final PostLikeService postLikeService;

  @PostMapping("/posts/{postId}/likes")
  public ResponseEntity<ApiResponse<LikeResponse>> addLike(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {
    LikeResponse response = postLikeService.addLike(postId, userId);
    return ResponseEntity.ok(ApiResponse.success(LIKE_ADDED, response));
  }

  @DeleteMapping("/posts/{postId}/likes")
  public ResponseEntity<ApiResponse<Void>> removeLike(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {
    postLikeService.removeLike(postId, userId);
    return ResponseEntity.status(NO_CONTENT).body(ApiResponse.success(LIKE_REMOVED, null));
  }
}