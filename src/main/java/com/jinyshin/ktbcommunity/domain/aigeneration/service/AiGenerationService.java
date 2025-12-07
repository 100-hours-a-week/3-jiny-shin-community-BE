package com.jinyshin.ktbcommunity.domain.aigeneration.service;

import com.jinyshin.ktbcommunity.domain.aigeneration.entity.AiGenerationLog;
import com.jinyshin.ktbcommunity.domain.aigeneration.repository.AiGenerationLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiGenerationService {

  private final AiGenerationLogRepository aiGenerationLogRepository;

  @Value("${ai.generation.daily-limit:5}")
  private int dailyLimit;

  @Transactional
  public void logUsage(Long userId, Long imageId) {
    aiGenerationLogRepository.save(new AiGenerationLog(userId, imageId));
  }

  @Transactional(readOnly = true)
  public RemainingUsage getRemaining(Long userId) {
    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

    long used = aiGenerationLogRepository.countByUserIdAndCreatedAtBetween(userId, startOfDay,
        endOfDay);
    long remaining = Math.max(0, dailyLimit - used);

    return new RemainingUsage(remaining, dailyLimit, used);
  }

  public record RemainingUsage(long remaining, long limit, long used) {
  }
}
