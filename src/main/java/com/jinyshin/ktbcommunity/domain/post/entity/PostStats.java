package com.jinyshin.ktbcommunity.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStats {

  @Id
  @Column(name = "post_id", nullable = false)
  private Long postId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_post_stats_post"))
  private Post post;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount = 0;

  @Column(name = "comment_count", nullable = false)
  private Integer commentCount = 0;

  @Column(name = "view_count", nullable = false)
  private Integer viewCount = 0;

  public PostStats(Post post) {
    this.post = post;
    this.likeCount = 0;
    this.commentCount = 0;
    this.viewCount = 0;
  }

  public void incrementViewCount() {
    this.viewCount++;
  }

  public void incrementLikeCount() {
    this.likeCount++;
  }

  public void decrementLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount--;
    }
  }

  public void incrementCommentCount() {
    this.commentCount++;
  }

  public void decrementCommentCount() {
    if (this.commentCount > 0) {
      this.commentCount--;
    }
  }

  public void decrementCommentCountBy(int amount) {
    this.commentCount = Math.max(0, this.commentCount - amount);
  }
}