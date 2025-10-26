package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;

public record CommentInfoResponse(
    Long id,
    String content,
    AuthorInfo author,
    Boolean isAuthor,
    Boolean isDeleted,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
