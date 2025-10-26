package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;

public record PostInfoResponse(
    Long id,
    String title,
    String content,
    AuthorInfo author,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer likeCount,
    Integer commentCount,
    Integer viewCount
) {

}
