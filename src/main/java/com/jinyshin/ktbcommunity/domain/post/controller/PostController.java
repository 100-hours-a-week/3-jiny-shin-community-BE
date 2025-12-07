package com.jinyshin.ktbcommunity.domain.post.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.MY_POSTS_RETRIEVED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_CREATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_DELETED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_RETRIEVED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POST_UPDATED;
import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.POSTS_RETRIEVED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.request.UpdatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostListResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.service.PostService;
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
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {

  private final PostService postService;

  @GetMapping("/posts")
  @Operation(
      summary = "게시글 목록 조회",
      description = "커서 기반 페이지네이션으로 게시글 목록을 조회합니다. 로그인 시 사용자별 좋아요 정보가 포함됩니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "게시글 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 요청 파라미터",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<PostListResponse>> getPosts(
      @Parameter(description = "커서 (이전 페이지의 마지막 게시글 ID)")
      @RequestParam(required = false) Long cursor,
      @Parameter(description = "정렬 방식 (asc: 오래된순, desc: 최신순)", example = "desc")
      @RequestParam(defaultValue = "desc") String sort,
      @Parameter(description = "페이지당 게시글 수", example = "10")
      @RequestParam(defaultValue = "10") int limit,
      @Parameter(hidden = true)
      @RequestAttribute(required = false) Long userId) {
    PostListResponse response = postService.getPosts(cursor, sort, limit, userId);
    return ResponseEntity.ok(BaseResponse.success(POSTS_RETRIEVED, response));
  }

  @GetMapping("/posts/me")
  @Operation(
      summary = "내 게시글 목록 조회",
      description = "현재 로그인한 사용자가 작성한 게시글 목록을 커서 기반 페이지네이션으로 조회합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "내 게시글 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<PostListResponse>> getMyPosts(
      @Parameter(description = "커서 (이전 페이지의 마지막 게시글 ID)")
      @RequestParam(required = false) Long cursor,
      @Parameter(description = "정렬 방식 (asc: 오래된순, desc: 최신순)", example = "desc")
      @RequestParam(defaultValue = "desc") String sort,
      @Parameter(description = "페이지당 게시글 수", example = "10")
      @RequestParam(defaultValue = "10") int limit,
      @Parameter(hidden = true)
      @RequestAttribute Long userId) {
    PostListResponse response = postService.getMyPosts(userId, cursor, sort, limit);
    return ResponseEntity.ok(BaseResponse.success(MY_POSTS_RETRIEVED, response));
  }

  @GetMapping("/posts/{postId}")
  @Operation(
      summary = "게시글 상세 조회",
      description = "특정 게시글의 상세 정보를 조회합니다. 조회 시 조회수가 증가하며, 로그인 시 좋아요 여부와 작성자 여부가 포함됩니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "게시글 상세 조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<PostDetailResponse>> getPostDetail(
      @Parameter(description = "게시글 ID", example = "1")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute(required = false) Long userId) {
    PostDetailResponse response = postService.getPostDetail(postId, userId);
    return ResponseEntity.ok(BaseResponse.success(POST_RETRIEVED, response));
  }

  @PostMapping("/posts")
  @Operation(
      summary = "게시글 작성",
      description = "새로운 게시글을 작성합니다. 로그인이 필요하며, 최대 5장의 이미지를 첨부할 수 있습니다."
  )
  @ApiResponse(
      responseCode = "201",
      description = "게시글 작성 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<CreatedPostResponse>> createPost(
      @Parameter(hidden = true)
      @RequestAttribute Long userId,
      @Parameter(description = "게시글 작성 요청 데이터")
      @Valid @RequestBody CreatePostRequest request) {
    CreatedPostResponse response = postService.createPost(userId, request);
    return ResponseEntity.status(CREATED).body(BaseResponse.success(POST_CREATED, response));
  }

  @PatchMapping("/posts/{postId}")
  @Operation(
      summary = "게시글 수정",
      description = "기존 게시글을 수정합니다. 작성자만 수정할 수 있으며, 제목, 본문, 이미지를 변경할 수 있습니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "게시글 수정 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "권한 없음 (작성자가 아님)",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "게시글을 찾을 수 없음",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<UpdatedPostResponse>> updatePost(
      @Parameter(description = "게시글 ID", example = "1")
      @PathVariable Long postId,
      @Parameter(hidden = true)
      @RequestAttribute Long userId,
      @Parameter(description = "게시글 수정 요청 데이터")
      @Valid @RequestBody UpdatePostRequest request) {
    UpdatedPostResponse response = postService.updatePost(postId, userId, request);
    return ResponseEntity.ok(BaseResponse.success(POST_UPDATED, response));
  }

  @DeleteMapping("/posts/{postId}")
  @Operation(
      summary = "게시글 삭제",
      description = "기존 게시글을 삭제합니다. 작성자만 삭제할 수 있습니다."
  )
  @ApiResponse(
      responseCode = "204",
      description = "게시글 삭제 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "403",
      description = "권한 없음 (작성자가 아님)",
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
    postService.deletePost(postId, userId);
    return ResponseEntity.status(NO_CONTENT).body(BaseResponse.success(POST_DELETED, null));
  }
}
