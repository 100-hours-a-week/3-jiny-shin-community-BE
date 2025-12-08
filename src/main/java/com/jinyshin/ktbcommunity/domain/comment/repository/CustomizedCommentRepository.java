package com.jinyshin.ktbcommunity.domain.comment.repository;

import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import java.util.List;
import java.util.Map;

public interface CustomizedCommentRepository {

  List<Comment> findCommentsWithCursor(Long postId, Long cursor, String sort, int limit);

  /**
   * 특정 사용자가 작성한 댓글의 게시글별 개수를 조회합니다.
   * 탈퇴 시 각 게시글의 commentCount를 감소시키기 위해 사용됩니다.
   *
   * @param authorId 사용자 ID
   * @return Map<postId, commentCount>
   */
  Map<Long, Long> countCommentsByAuthorGroupByPost(Long authorId);
}