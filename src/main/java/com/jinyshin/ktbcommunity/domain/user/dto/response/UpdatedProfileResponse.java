package com.jinyshin.ktbcommunity.domain.user.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import java.time.LocalDateTime;

public record UpdatedProfileResponse(
        Long id,
        String email,
        String nickname,
        ImageUrlsResponse profileImageUrls,
        LocalDateTime updatedAt
) {
}
