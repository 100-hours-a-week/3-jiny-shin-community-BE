package com.jinyshin.ktbcommunity.domain.post.dto.request;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.MAX_POST_IMAGES;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_CONTENT_MAX;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_CONTENT_MIN;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_TITLE_MAX;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_TITLE_MIN;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = POST_TITLE_MIN, max = POST_TITLE_MAX, message = "제목은 2자 이상 26자 이하여야 합니다.")
    String title,

    @NotBlank(message = "본문은 필수입니다.")
    @Size(min = POST_CONTENT_MIN, max = POST_CONTENT_MAX, message = "본문은 2자 이상 10000자 이하여야 합니다.")
    String content,

    @Size(max = MAX_POST_IMAGES, message = "이미지는 최대 5장까지 첨부할 수 있습니다.")
    List<Long> imageIds,

    Long primaryImageId  // 사용자가 선택한 대표 이미지 ID (이미지 없으면 null)
) {

}
