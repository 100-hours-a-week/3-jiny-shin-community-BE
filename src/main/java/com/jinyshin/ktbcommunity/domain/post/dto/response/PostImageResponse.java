package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;

public record PostImageResponse(
    Long imageId,
    ImageUrlsResponse imageUrls,
    Integer position,
    Boolean isPrimary
) {

}