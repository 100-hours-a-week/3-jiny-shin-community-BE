package com.jinyshin.ktbcommunity.domain.post.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POSTS_RETRIEVED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_CREATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_DELETED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_RETRIEVED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_UPDATED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.request.UpdatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostListResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.service.PostService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreatedPostResponse>> createPost(
      @RequestAttribute Long userId,
      @Valid @RequestBody CreatePostRequest request) {
    CreatedPostResponse response = postService.createPost(userId, request);
    return ResponseEntity.status(CREATED).body(ApiResponse.success(POST_CREATED, response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PostListResponse>> getPosts(
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false, defaultValue = "desc") String sort,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestAttribute Long userId) {
    PostListResponse response = postService.getPosts(cursor, sort, limit, userId);
    return ResponseEntity.ok(ApiResponse.success(POSTS_RETRIEVED, response));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {
    PostDetailResponse response = postService.getPostDetail(postId, userId);
    return ResponseEntity.ok(ApiResponse.success(POST_RETRIEVED, response));
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<ApiResponse<UpdatedPostResponse>> updatePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId,
      @Valid @RequestBody UpdatePostRequest request) {
    UpdatedPostResponse response = postService.updatePost(postId, userId, request);
    return ResponseEntity.ok(ApiResponse.success(POST_UPDATED, response));
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> deletePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {
    postService.deletePost(postId, userId);
    return ResponseEntity.status(NO_CONTENT).body(ApiResponse.success(POST_DELETED, null));
  }
}
