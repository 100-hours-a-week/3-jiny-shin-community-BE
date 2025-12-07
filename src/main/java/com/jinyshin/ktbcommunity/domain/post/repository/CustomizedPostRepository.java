package com.jinyshin.ktbcommunity.domain.post.repository;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;

public interface CustomizedPostRepository {

  List<Post> findPostsWithCursor(Long cursor, String sort, int limit);

  List<Post> findMyPostsWithCursor(Long userId, Long cursor, String sort, int limit);

  Optional<Post> findByIdWithDetails(Long postId);

  Optional<Post> findByIdWithStats(Long postId);
}
