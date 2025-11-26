package com.jinyshin.ktbcommunity.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CreatedPostResponse(
    Long postId,
    String title,
    String content,
    List<PostImageResponse> images,
    LocalDateTime createdAt
) {

}
