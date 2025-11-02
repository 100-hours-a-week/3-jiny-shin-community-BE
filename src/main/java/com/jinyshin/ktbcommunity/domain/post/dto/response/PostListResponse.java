package com.jinyshin.ktbcommunity.domain.post.dto.response;

import java.util.List;

public record PostListResponse(
    boolean hasNext,
    Long nextCursor,
    List<PostInfoResponse> posts
) {

}