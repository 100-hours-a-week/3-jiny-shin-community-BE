package com.jinyshin.ktbcommunity.domain.user.dto.response;

import java.time.LocalDateTime;

public record UpdatedProfileResponse(
        Long id,
        String email,
        String nickname,
        String profileImageUrl,
        LocalDateTime updatedAt
) {
}
