package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;

public record CreatedCommentResponse(
    Long id,
    String content,
    AuthorInfo author,
    LocalDateTime createdAt
) {

}
