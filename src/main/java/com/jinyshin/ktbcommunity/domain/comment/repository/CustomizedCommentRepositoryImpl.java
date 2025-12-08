package com.jinyshin.ktbcommunity.domain.comment.repository;

import static com.jinyshin.ktbcommunity.domain.comment.entity.QComment.comment;
import static com.jinyshin.ktbcommunity.domain.user.entity.QUser.user;

import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomizedCommentRepositoryImpl implements CustomizedCommentRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findCommentsWithCursor(Long postId, Long cursor, String sort, int limit) {
    BooleanExpression cursorCondition = getCursorCondition(cursor, sort);

    return queryFactory
        .selectFrom(comment)
        .join(comment.author, user).fetchJoin()
        .where(
            comment.post.postId.eq(postId),
            cursorCondition
        )
        .orderBy(sort.equals("asc") ? comment.commentId.asc() : comment.commentId.desc())
        .limit(limit + 1)
        .fetch();
  }

  @Override
  public Map<Long, Long> countCommentsByAuthorGroupByPost(Long authorId) {
    List<Tuple> results = queryFactory
        .select(comment.post.postId, comment.count())
        .from(comment)
        .where(
            comment.author.userId.eq(authorId),
            comment.deletedAt.isNull()
        )
        .groupBy(comment.post.postId)
        .fetch();

    return results.stream()
        .collect(Collectors.toMap(
            tuple -> Objects.requireNonNull(tuple.get(comment.post.postId)),
            tuple -> Objects.requireNonNull(tuple.get(comment.count()))
        ));
  }

  private BooleanExpression getCursorCondition(Long cursor, String sort) {
    if (cursor == null) {
      return null;
    }
    return sort.equals("asc") ? comment.commentId.gt(cursor) : comment.commentId.lt(cursor);
  }
}