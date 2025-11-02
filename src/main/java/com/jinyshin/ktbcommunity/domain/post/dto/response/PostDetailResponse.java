package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;

public record PostDetailResponse(
    Long id,
    String title,
    String content,
    AuthorInfo author,
    Integer viewCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean isAuthor
) {

}
