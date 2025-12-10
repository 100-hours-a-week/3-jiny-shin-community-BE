package com.jinyshin.ktbcommunity.domain.feedback.repository;

import com.jinyshin.ktbcommunity.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}