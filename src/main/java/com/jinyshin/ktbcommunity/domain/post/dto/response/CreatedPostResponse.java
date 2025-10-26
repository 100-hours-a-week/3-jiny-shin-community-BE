package com.jinyshin.ktbcommunity.domain.post.dto.response;

import java.time.LocalDateTime;

public record CreatedPostResponse(
    Long id,
    String title,
    LocalDateTime createdAt
) {

}
