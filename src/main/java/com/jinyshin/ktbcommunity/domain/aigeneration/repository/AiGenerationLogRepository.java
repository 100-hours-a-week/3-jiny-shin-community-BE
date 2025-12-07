package com.jinyshin.ktbcommunity.domain.aigeneration.repository;

import com.jinyshin.ktbcommunity.domain.aigeneration.entity.AiGenerationLog;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiGenerationLogRepository extends JpaRepository<AiGenerationLog, Long> {

  long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
