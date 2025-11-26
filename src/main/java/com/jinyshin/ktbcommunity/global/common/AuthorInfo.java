package com.jinyshin.ktbcommunity.global.common;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;

public record AuthorInfo(
    Long id,
    String nickname,
    ImageUrlsResponse profileImageUrls
) {

}