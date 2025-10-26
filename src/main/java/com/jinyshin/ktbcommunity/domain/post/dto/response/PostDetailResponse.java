package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
    Long id,
    String title,
    String content,
    AuthorInfo author,
    Integer likeCount,
    Integer commentCount,
    Integer viewCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean isLiked,
    Boolean isAuthor,
    List<String> contentImageUrls
) {

}
