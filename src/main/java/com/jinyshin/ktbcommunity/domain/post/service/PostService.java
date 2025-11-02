package com.jinyshin.ktbcommunity.domain.post.service;

import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.request.UpdatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostListResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;

public interface PostService {

  CreatedPostResponse createPost(Long userId, CreatePostRequest request);

  PostListResponse getPosts(Long cursor, String sort, int limit, Long currentUserId);

  PostDetailResponse getPostDetail(Long postId, Long currentUserId);

  UpdatedPostResponse updatePost(Long postId, Long userId, UpdatePostRequest request);

  void deletePost(Long postId, Long userId);
}