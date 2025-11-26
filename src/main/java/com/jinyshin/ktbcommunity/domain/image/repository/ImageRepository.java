package com.jinyshin.ktbcommunity.domain.image.repository;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByImageIdIn(List<Long> imageIds);

  // TEMP 고아 이미지 조회: 2시간 지난 TEMP 상태 이미지
  List<Image> findByStatusAndCreatedAtBefore(ImageStatus status, LocalDateTime cutoffDate);

  // DELETED 이미지 조회: 7일 지난 DELETED 상태 이미지
  List<Image> findByStatusAndDeletedAtBefore(ImageStatus status, LocalDateTime cutoffDate);
}