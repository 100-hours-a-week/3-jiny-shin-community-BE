package com.jinyshin.ktbcommunity.global.common;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작성자 정보")
public record AuthorInfo(
    @Schema(description = "작성자 ID", example = "1")
    Long id,

    @Schema(description = "작성자 닉네임", example = "홍길동")
    String nickname,

    @Schema(description = "프로필 이미지 URL 정보")
    ImageUrlsResponse profileImageUrls
) {

}