package com.jinyshin.ktbcommunity.domain.post.repository;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import java.util.List;

public interface CustomizedPostRepository {

  List<Post> findPostsWithCursor(Long cursor, String sort, int limit);
}
