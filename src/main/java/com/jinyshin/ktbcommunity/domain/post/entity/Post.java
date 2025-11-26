package com.jinyshin.ktbcommunity.domain.post.entity;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_posts_deleted_at", columnList = "deleted_at"),
    @Index(name = "idx_posts_author", columnList = "author_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE post_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "title", nullable = false, length = 26)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posts_author"))
  private User author;

  @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private PostStats postStats;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("position ASC")
  private List<PostImage> postImages = new ArrayList<>();

  public Post(String title, String content, User author) {
    this.title = title;
    this.content = content;
    this.author = author;
  }

  @PrePersist
  protected void onCreate() {
    if (postStats == null) {
      postStats = new PostStats(this);
    }
  }

  public void update(String title, String content) {
    if (title != null) {
      this.title = title;
    }
    if (content != null) {
      this.content = content;
    }
  }

  public void addImage(Image image, int position) {
    PostImage postImage = new PostImage(this, image, position);
    this.postImages.add(postImage);
  }

  public void clearImages() {
    this.postImages.clear();
  }
}