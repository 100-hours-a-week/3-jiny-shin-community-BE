package com.jinyshin.ktbcommunity.domain.post.entity;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_images", uniqueConstraints = {
    @UniqueConstraint(name = "uk_post_images_post_image", columnNames = {"post_id", "image_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_image_id", nullable = false)
  private Long postImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_images_post"))
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_images_image"))
  private Image image;

  @Column(name = "position", nullable = false)
  private int position = 1;

  @Setter
  @Column(name = "is_primary", nullable = false)
  private boolean isPrimary = false;

  public PostImage(Post post, Image image, Integer position) {
    this.post = post;
    this.image = image;
    this.position = position;
  }
}