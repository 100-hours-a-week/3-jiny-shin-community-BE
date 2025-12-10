package com.jinyshin.ktbcommunity.domain.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "피드백 생성 요청")
public record CreateFeedbackRequest(
    @Schema(description = "피드백 내용", example = "앱 사용 중 버그를 발견했습니다.")
    @NotBlank(message = "피드백 내용은 필수입니다.")
    @Size(max = 2000, message = "피드백 내용은 최대 2000자까지 입력 가능합니다.")
    String content,

    @Schema(description = "앱 버전", example = "1.0.0")
    @Size(max = 50, message = "앱 버전은 최대 50자까지 입력 가능합니다.")
    String appVersion,

    @Schema(description = "플랫폼", example = "iOS")
    @Size(max = 50, message = "플랫폼은 최대 50자까지 입력 가능합니다.")
    String platform,

    @Schema(description = "생성 시간 (클라이언트 기준, 서버에서 재정의 가능)", example = "2024-12-10T12:00:00")
    String createdAt
) {

}