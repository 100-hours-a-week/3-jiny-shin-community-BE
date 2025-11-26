package com.jinyshin.ktbcommunity.domain.user.dto.response;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "프로필 수정 응답")
public record UpdatedProfileResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "프로필 이미지 URL들 (다중 포맷)")
        ImageUrlsResponse profileImageUrls,

        @Schema(description = "수정 일시", example = "2024-01-01T12:00:00")
        LocalDateTime updatedAt
) {
}
