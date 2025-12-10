package com.jinyshin.ktbcommunity.domain.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 생성 응답")
public record FeedbackResponse(
    @Schema(description = "상태", example = "ok")
    String status
) {

  public static FeedbackResponse ok() {
    return new FeedbackResponse("ok");
  }
}