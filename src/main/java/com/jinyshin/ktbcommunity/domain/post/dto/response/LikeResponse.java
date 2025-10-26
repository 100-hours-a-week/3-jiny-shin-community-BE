package com.jinyshin.ktbcommunity.domain.post.dto.response;

public record LikeResponse(
    Long postId,
    Integer likeCount,
    Boolean isLiked
) {

}