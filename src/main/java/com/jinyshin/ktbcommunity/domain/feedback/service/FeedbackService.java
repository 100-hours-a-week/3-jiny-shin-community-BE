package com.jinyshin.ktbcommunity.domain.feedback.service;

import com.jinyshin.ktbcommunity.domain.feedback.dto.request.CreateFeedbackRequest;
import com.jinyshin.ktbcommunity.domain.feedback.dto.response.FeedbackResponse;

public interface FeedbackService {

  FeedbackResponse createFeedback(CreateFeedbackRequest request);
}