package com.jinyshin.ktbcommunity.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답 DTO")
public record LikeResponse(
    @Schema(description = "게시글 ID", example = "1")
    Long postId,
    @Schema(description = "총 좋아요 수", example = "10")
    Integer likeCount,
    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    Boolean isLiked
) {

}