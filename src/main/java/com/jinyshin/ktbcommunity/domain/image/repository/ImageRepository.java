package com.jinyshin.ktbcommunity.domain.image.repository;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByImageIdIn(List<Long> imageIds);

  // 고아 이미지 조회: 게시물 및 프로필 사진에 연결되지 않고 일정 시간이 지난 이미지
  @Query("SELECT i FROM Image i "
      + "WHERE NOT EXISTS (SELECT 1 FROM PostImage pi WHERE pi.image.imageId = i.imageId) "
      + "AND NOT EXISTS (SELECT 1 FROM User u WHERE u.profileImage.imageId = i.imageId) "
      + "AND i.createdAt < :threshold")
  List<Image> findOrphanImages(@Param("threshold") LocalDateTime threshold);

  // 완전 삭제 대상 조회: soft delete 후 일정 기간이 지난 이미지
  List<Image> findByDeletedAtBefore(LocalDateTime dateTime);
}