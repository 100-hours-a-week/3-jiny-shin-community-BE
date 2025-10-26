package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import java.time.LocalDateTime;

public record UpdatedCommentResponse(
    Long id,
    String content,
    LocalDateTime updatedAt
) {

}
