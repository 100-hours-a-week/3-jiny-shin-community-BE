package com.jinyshin.ktbcommunity.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 목록 응답")
public record PostListResponse(
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(description = "다음 페이지 커서 (마지막 게시글 ID)", example = "10")
    Long nextCursor,

    @Schema(description = "게시글 목록")
    List<PostInfoResponse> posts
) {

}