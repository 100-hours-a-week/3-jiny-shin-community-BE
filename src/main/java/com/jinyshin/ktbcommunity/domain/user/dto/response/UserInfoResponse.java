package com.jinyshin.ktbcommunity.domain.user.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;

public record UserInfoResponse(
        Long userId,
        String email,
        String nickname,
        ImageUrlsResponse profileImageUrls
) {
}