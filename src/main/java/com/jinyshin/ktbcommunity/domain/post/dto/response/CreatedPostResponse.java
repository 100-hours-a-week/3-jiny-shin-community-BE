package com.jinyshin.ktbcommunity.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시글 작성 응답")
public record CreatedPostResponse(
    @Schema(description = "생성된 게시글 ID", example = "1")
    Long postId,

    @Schema(description = "게시글 제목", example = "안녕하세요 첫 게시글입니다")
    String title,

    @Schema(description = "게시글 본문", example = "이것은 게시글의 본문 내용입니다.")
    String content,

    @Schema(description = "게시글 이미지 목록")
    List<PostImageResponse> images,

    @Schema(description = "생성 일시", example = "2025-01-01T00:00:00")
    LocalDateTime createdAt
) {

}
