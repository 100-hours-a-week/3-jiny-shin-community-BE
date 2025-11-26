package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "댓글 목록 응답")
public record CommentListResponse(
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(description = "다음 페이지 커서 (다음 요청 시 사용)", example = "10")
    Long nextCursor,

    @Schema(description = "현재 페이지 댓글 수", example = "10")
    Integer count,

    @Schema(description = "댓글 목록")
    List<CommentInfoResponse> comments
) {

}