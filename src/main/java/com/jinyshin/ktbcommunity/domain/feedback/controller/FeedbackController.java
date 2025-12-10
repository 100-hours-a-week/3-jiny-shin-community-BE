package com.jinyshin.ktbcommunity.domain.feedback.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.FEEDBACK_CREATED;

import com.jinyshin.ktbcommunity.domain.feedback.dto.request.CreateFeedbackRequest;
import com.jinyshin.ktbcommunity.domain.feedback.dto.response.FeedbackResponse;
import com.jinyshin.ktbcommunity.domain.feedback.service.FeedbackService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 API")
@Slf4j
public class FeedbackController {

  private final FeedbackService feedbackService;

  @PostMapping
  @Operation(summary = "피드백 생성", description = "사용자 피드백을 생성하고 개발자에게 이메일 알림을 발송합니다.")
  public ResponseEntity<BaseResponse<FeedbackResponse>> createFeedback(
      @Valid @RequestBody CreateFeedbackRequest request) {
    log.info("[FeedbackController] 피드백 생성 요청");
    FeedbackResponse response = feedbackService.createFeedback(request);
    return ResponseEntity.ok(BaseResponse.success(FEEDBACK_CREATED, response));
  }
}