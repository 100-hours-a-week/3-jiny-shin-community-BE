package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "댓글 정보")
public record CommentInfoResponse(
    @Schema(description = "댓글 ID", example = "1")
    Long id,

    @Schema(description = "댓글 내용", example = "좋은 게시글이네요!")
    String content,

    @Schema(description = "작성자 정보")
    AuthorInfo author,

    @Schema(description = "현재 사용자가 작성자인지 여부", example = "true")
    Boolean isAuthor,

    @Schema(description = "삭제 여부", example = "false")
    Boolean isDeleted,

    @Schema(description = "작성 시간", example = "2024-01-01T12:00:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 시간", example = "2024-01-01T12:30:00")
    LocalDateTime updatedAt
) {

}
