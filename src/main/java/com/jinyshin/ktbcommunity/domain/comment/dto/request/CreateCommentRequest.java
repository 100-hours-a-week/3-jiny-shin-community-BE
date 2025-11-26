package com.jinyshin.ktbcommunity.domain.comment.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.COMMENT_CONTENT_MAX;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.COMMENT_CONTENT_MIN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 작성 요청")
public record CreateCommentRequest(
    @Schema(description = "댓글 내용", example = "좋은 게시글이네요!")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = COMMENT_CONTENT_MIN, max = COMMENT_CONTENT_MAX, message = "댓글은 1자 이상 1000자 이하여야 합니다.")
    String contents
) {

}
