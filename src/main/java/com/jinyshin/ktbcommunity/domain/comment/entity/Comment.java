package com.jinyshin.ktbcommunity.domain.comment.entity;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.DELETED_COMMENT_MESSAGE;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_comments_author", columnList = "author_id"),
        @Index(name = "idx_comments_post", columnList = "post_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comments SET deleted_at = NOW() WHERE comment_id = ?")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_author"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_post"))
    private Post post;

    public Comment(String content, User author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public String getDisplayContent() {
        return isDeleted() ? DELETED_COMMENT_MESSAGE : this.content;
    }
}