package com.jinyshin.ktbcommunity.domain.post.service;

import com.jinyshin.ktbcommunity.domain.post.dto.response.LikeResponse;

public interface PostLikeService {

  LikeResponse addLike(Long postId, Long userId);

  void removeLike(Long postId, Long userId);
}