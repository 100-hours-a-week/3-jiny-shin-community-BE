package com.jinyshin.ktbcommunity.domain.post.dto.response;

import java.time.LocalDateTime;

public record UpdatedPostResponse(
    Long id,
    String title,
    String content,
    LocalDateTime updatedAt
) {

}
