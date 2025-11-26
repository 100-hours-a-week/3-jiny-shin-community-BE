package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.time.LocalDateTime;

public record PostInfoResponse(
    Long postId,
    String title,
    String content,
    AuthorInfo author,
    ImageUrlsResponse thumbnailUrls,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer likeCount,
    Integer commentCount,
    Integer viewCount
) {

}
