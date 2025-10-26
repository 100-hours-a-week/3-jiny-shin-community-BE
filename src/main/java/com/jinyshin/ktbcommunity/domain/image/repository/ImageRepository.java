package com.jinyshin.ktbcommunity.domain.image.repository;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByImageIdIn(List<Long> imageIds);
}