package com.jinyshin.ktbcommunity.domain.feedback.service;

import com.jinyshin.ktbcommunity.domain.feedback.dto.request.CreateFeedbackRequest;
import com.jinyshin.ktbcommunity.domain.feedback.dto.response.FeedbackResponse;
import com.jinyshin.ktbcommunity.domain.feedback.entity.Feedback;
import com.jinyshin.ktbcommunity.domain.feedback.repository.FeedbackRepository;
import com.jinyshin.ktbcommunity.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final EmailService emailService;

  @Override
  @Transactional
  public FeedbackResponse createFeedback(CreateFeedbackRequest request) {
    log.info("[FeedbackService] 피드백 생성 요청. content 길이: {}, platform: {}, appVersion: {}",
        request.content().length(),
        request.platform(),
        request.appVersion());

    Feedback feedback = Feedback.builder()
        .content(request.content())
        .appVersion(request.appVersion())
        .platform(request.platform())
        .build();

    Feedback savedFeedback = feedbackRepository.save(feedback);
    log.info("[FeedbackService] 피드백 저장 완료. ID: {}", savedFeedback.getId());

    // 이메일 발송 (실패해도 API 응답에 영향 없음)
    emailService.sendFeedbackNotification(savedFeedback);

    return FeedbackResponse.ok();
  }
}