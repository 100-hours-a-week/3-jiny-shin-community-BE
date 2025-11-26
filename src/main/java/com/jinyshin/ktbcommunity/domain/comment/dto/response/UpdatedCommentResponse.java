package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "댓글 수정 응답")
public record UpdatedCommentResponse(
    @Schema(description = "댓글 ID", example = "1")
    Long id,

    @Schema(description = "수정된 댓글 내용", example = "댓글 내용을 수정합니다.")
    String content,

    @Schema(description = "수정 시간", example = "2024-01-01T12:30:00")
    LocalDateTime updatedAt
) {

}
