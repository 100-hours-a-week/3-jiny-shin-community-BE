package com.jinyshin.ktbcommunity.domain.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import com.jinyshin.ktbcommunity.domain.feedback.dto.request.CreateFeedbackRequest;
import com.jinyshin.ktbcommunity.domain.feedback.dto.response.FeedbackResponse;
import com.jinyshin.ktbcommunity.domain.feedback.entity.Feedback;
import com.jinyshin.ktbcommunity.domain.feedback.repository.FeedbackRepository;
import com.jinyshin.ktbcommunity.domain.feedback.service.FeedbackService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FeedbackServiceTest {

  @Autowired
  private FeedbackService feedbackService;

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Nested
  @DisplayName("피드백 생성 테스트")
  class CreateFeedbackTest {

    @Test
    @DisplayName("모든 필드가 있는 피드백 생성 성공")
    void createFeedback_withAllFields_success() {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "앱에서 버그를 발견했습니다. 로그인 버튼이 작동하지 않습니다.",
          "1.0.0",
          "iOS",
          "2024-12-10T12:00:00"
      );

      // When
      FeedbackResponse response = feedbackService.createFeedback(request);

      // Then
      assertThat(response.status()).isEqualTo("ok");

      // DB 저장 확인
      Optional<Feedback> savedFeedback = feedbackRepository.findAll().stream().findFirst();
      assertThat(savedFeedback).isPresent();
      assertThat(savedFeedback.get().getContent()).isEqualTo(request.content());
      assertThat(savedFeedback.get().getAppVersion()).isEqualTo(request.appVersion());
      assertThat(savedFeedback.get().getPlatform()).isEqualTo(request.platform());
      assertThat(savedFeedback.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("필수 필드만 있는 피드백 생성 성공")
    void createFeedback_withRequiredFieldsOnly_success() {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "간단한 피드백입니다.",
          null,
          null,
          null
      );

      // When
      FeedbackResponse response = feedbackService.createFeedback(request);

      // Then
      assertThat(response.status()).isEqualTo("ok");

      Optional<Feedback> savedFeedback = feedbackRepository.findAll().stream().findFirst();
      assertThat(savedFeedback).isPresent();
      assertThat(savedFeedback.get().getContent()).isEqualTo(request.content());
      assertThat(savedFeedback.get().getAppVersion()).isNull();
      assertThat(savedFeedback.get().getPlatform()).isNull();
    }

    @Test
    @DisplayName("긴 피드백 내용 저장 성공")
    void createFeedback_withLongContent_success() {
      // Given
      String longContent = "A".repeat(2000);
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          longContent,
          "2.0.0",
          "Android",
          null
      );

      // When
      FeedbackResponse response = feedbackService.createFeedback(request);

      // Then
      assertThat(response.status()).isEqualTo("ok");

      Optional<Feedback> savedFeedback = feedbackRepository.findAll().stream().findFirst();
      assertThat(savedFeedback).isPresent();
      assertThat(savedFeedback.get().getContent()).hasSize(2000);
    }
  }
}