package com.jinyshin.ktbcommunity.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.*;

public record CreateCommentRequest(
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = COMMENT_CONTENT_MIN, max = COMMENT_CONTENT_MAX, message = "댓글은 1자 이상 1000자 이하여야 합니다.")
    String contents
) {

}
