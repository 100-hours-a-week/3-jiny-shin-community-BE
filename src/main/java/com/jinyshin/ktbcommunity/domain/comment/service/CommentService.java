package com.jinyshin.ktbcommunity.domain.comment.service;

import com.jinyshin.ktbcommunity.domain.comment.dto.request.CreateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.request.UpdateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CommentListResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CreatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.UpdatedCommentResponse;

public interface CommentService {

  CreatedCommentResponse createComment(Long postId, Long userId, CreateCommentRequest request);

  CommentListResponse getComments(Long postId, Long cursor, String sort, int limit,
      Long currentUserId);

  UpdatedCommentResponse updateComment(Long commentId, Long userId, UpdateCommentRequest request);

  void deleteComment(Long commentId, Long userId);
}
