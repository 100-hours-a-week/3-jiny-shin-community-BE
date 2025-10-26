package com.jinyshin.ktbcommunity.domain.post.repository;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.entity.PostLike;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  boolean existsByPostAndUser(Post post, User user);

  Optional<PostLike> findByPostAndUser(Post post, User user);
}