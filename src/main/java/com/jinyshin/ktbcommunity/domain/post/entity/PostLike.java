package com.jinyshin.ktbcommunity.domain.post.entity;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_likes_user_post", columnNames = {"user_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_post_likes_user", columnList = "user_id"),
                @Index(name = "idx_post_likes_post", columnList = "post_id")
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_likes_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_likes_post"))
    private Post post;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
