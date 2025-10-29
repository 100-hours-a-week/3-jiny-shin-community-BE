package com.jinyshin.ktbcommunity.domain.user.dto.response;

public record UserInfoResponse(
        Long userId,
        String email,
        String nickname
) {
}