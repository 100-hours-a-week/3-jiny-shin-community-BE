package com.jinyshin.ktbcommunity.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시글 수정 응답")
public record UpdatedPostResponse(
    @Schema(description = "게시글 ID", example = "1")
    Long postId,

    @Schema(description = "수정된 게시글 제목", example = "수정된 게시글 제목")
    String title,

    @Schema(description = "수정된 게시글 본문", example = "수정된 게시글 본문입니다.")
    String content,

    @Schema(description = "게시글 이미지 목록")
    List<PostImageResponse> images,

    @Schema(description = "수정 일시", example = "2025-01-02T00:00:00")
    LocalDateTime updatedAt
) {

}
