package com.jinyshin.ktbcommunity.domain.comment.dto.response;

import java.util.List;

public record CommentListResponse(
    boolean hasNext,
    Long nextCursor,
    Integer count,
    List<CommentInfoResponse> comments
) {

}