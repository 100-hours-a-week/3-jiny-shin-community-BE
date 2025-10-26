package com.jinyshin.ktbcommunity.domain.post.dto.request;

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

    List<Long> imageIds
) {

}
