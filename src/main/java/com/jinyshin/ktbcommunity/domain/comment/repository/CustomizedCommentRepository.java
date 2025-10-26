package com.jinyshin.ktbcommunity.domain.comment.repository;

import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import java.util.List;

public interface CustomizedCommentRepository {

  List<Comment> findCommentsWithCursor(Long postId, Long cursor, String sort, int limit);
}