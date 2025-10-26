package com.jinyshin.ktbcommunity.domain.post.repository;

import static com.jinyshin.ktbcommunity.domain.post.entity.QPost.post;
import static com.jinyshin.ktbcommunity.domain.post.entity.QPostStats.postStats;
import static com.jinyshin.ktbcommunity.domain.user.entity.QUser.user;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Post> findPostsWithCursor(Long cursor, String sort, int limit) {
    BooleanExpression cursorCondition = getCursorCondition(cursor, sort);

    return queryFactory
        .selectFrom(post)
        .join(post.author, user).fetchJoin()
        .join(post.postStats, postStats).fetchJoin()
        .where(cursorCondition)
        .orderBy(sort.equals("asc") ? post.postId.asc() : post.postId.desc())
        .limit(limit + 1)
        .fetch();
  }

  private BooleanExpression getCursorCondition(Long cursor, String sort) {
    if (cursor == null) {
      return null;
    }
    return sort.equals("asc") ? post.postId.gt(cursor) : post.postId.lt(cursor);
  }
}