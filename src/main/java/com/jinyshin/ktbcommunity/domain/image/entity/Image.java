package com.jinyshin.ktbcommunity.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "images", indexes = {
        @Index(name = "idx_images_deleted_at", columnList = "deleted_at")
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

    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Image(String s3Key) {
        this.s3Key = s3Key;
    }
}