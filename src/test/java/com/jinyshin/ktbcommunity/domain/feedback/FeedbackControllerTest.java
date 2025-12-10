package com.jinyshin.ktbcommunity.domain.feedback;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyshin.ktbcommunity.domain.feedback.dto.request.CreateFeedbackRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FeedbackControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  @DisplayName("POST /api/feedback - 피드백 생성")
  class CreateFeedbackApiTest {

    @Test
    @DisplayName("유효한 요청으로 피드백 생성 성공")
    void createFeedback_validRequest_returns200() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "앱 사용 중 불편한 점이 있었습니다.",
          "1.0.0",
          "iOS",
          "2024-12-10T12:00:00"
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("feedback_created"))
          .andExpect(jsonPath("$.data.status").value("ok"));
    }

    @Test
    @DisplayName("content가 없으면 400 에러 반환")
    void createFeedback_emptyContent_returns400() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "",
          "1.0.0",
          "iOS",
          null
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    @DisplayName("content가 null이면 400 에러 반환")
    void createFeedback_nullContent_returns400() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          null,
          "1.0.0",
          "iOS",
          null
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    @DisplayName("content가 공백만 있으면 400 에러 반환")
    void createFeedback_whitespaceOnlyContent_returns400() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "   ",
          "1.0.0",
          "iOS",
          null
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    @DisplayName("content가 2000자를 초과하면 400 에러 반환")
    void createFeedback_contentTooLong_returns400() throws Exception {
      // Given
      String tooLongContent = "A".repeat(2001);
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          tooLongContent,
          "1.0.0",
          "iOS",
          null
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    @DisplayName("optional 필드 없이 피드백 생성 성공")
    void createFeedback_withoutOptionalFields_returns200() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "필수 필드만 있는 피드백입니다.",
          null,
          null,
          null
      );

      // When & Then
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status").value("ok"));
    }

    @Test
    @DisplayName("인증 없이 피드백 API 접근 가능")
    void createFeedback_withoutAuthentication_returns200() throws Exception {
      // Given
      CreateFeedbackRequest request = new CreateFeedbackRequest(
          "비로그인 사용자의 피드백입니다.",
          "1.0.0",
          "Web",
          null
      );

      // When & Then (세션 없이 요청)
      mockMvc.perform(post("/api/feedback")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isOk());
    }
  }
}