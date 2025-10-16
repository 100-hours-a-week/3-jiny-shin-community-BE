package com.jinyshin.ktbcommunity.domain.post.entity;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer position = 0;

    public PostImage(Post post, Image image, Integer position) {
        this.post = post;
        this.image = image;
        this.position = position;
    }
}