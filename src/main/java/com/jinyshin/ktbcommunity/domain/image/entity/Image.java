package com.jinyshin.ktbcommunity.domain.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "images", indexes = {
    @Index(name = "idx_images_deleted_at", columnList = "deleted_at"),
    @Index(name = "idx_images_stored_filename", columnList = "stored_filename")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE images SET deleted_at = NOW() WHERE image_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id", nullable = false)
  private Long imageId;

  @Column(name = "stored_filename", nullable = false, length = 255)
  private String storedFilename;

  @Column(name = "original_extension", nullable = false, length = 10)
  private String originalExtension;

  @Column(name = "s3_path", nullable = false, length = 100)
  private String s3Path;

  @Enumerated(EnumType.STRING)
  @Column(name = "image_type", nullable = false, length = 20)
  private ImageType imageType;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public Image(String storedFilename, String originalExtension, String s3Path, ImageType imageType) {
    this.storedFilename = storedFilename;
    this.originalExtension = originalExtension;
    this.s3Path = s3Path;
    this.imageType = imageType;
  }
}