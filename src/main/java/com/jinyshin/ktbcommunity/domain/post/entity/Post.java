package com.jinyshin.ktbcommunity.domain.post.entity;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

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

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posts_author"))
    private User author;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostStats postStats;

    @PrePersist
    protected void onCreate() {
        if (postStats == null) {
            postStats = new PostStats(this);
        }
    }

    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }
}