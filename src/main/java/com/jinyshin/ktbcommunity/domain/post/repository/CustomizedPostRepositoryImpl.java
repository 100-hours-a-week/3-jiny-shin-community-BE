package com.jinyshin.ktbcommunity.domain.post.repository;

import static com.jinyshin.ktbcommunity.domain.post.entity.QPost.post;
import static com.jinyshin.ktbcommunity.domain.post.entity.QPostImage.postImage;
import static com.jinyshin.ktbcommunity.domain.post.entity.QPostStats.postStats;
import static com.jinyshin.ktbcommunity.domain.user.entity.QUser.user;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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

  @Override
  public Optional<Post> findByIdWithDetails(Long postId) {
    Post foundPost = queryFactory
        .selectFrom(post)
        .leftJoin(post.author, user).fetchJoin()
        .leftJoin(user.profileImage).fetchJoin()
        .leftJoin(post.postStats, postStats).fetchJoin()
        .where(post.postId.eq(postId))
        .fetchOne();

    if (foundPost != null) {
      queryFactory
          .selectFrom(postImage)
          .leftJoin(postImage.image).fetchJoin()
          .where(postImage.post.eq(foundPost))
          .orderBy(postImage.position.asc())
          .fetch();
    }

    return Optional.ofNullable(foundPost);
  }

  @Override
  public Optional<Post> findByIdWithStats(Long postId) {
    Post foundPost = queryFactory
        .selectFrom(post)
        .join(post.postStats, postStats).fetchJoin()
        .where(post.postId.eq(postId))
        .fetchOne();

    return Optional.ofNullable(foundPost);
  }

  private BooleanExpression getCursorCondition(Long cursor, String sort) {
    if (cursor == null) {
      return null;
    }
    return sort.equals("asc") ? post.postId.gt(cursor) : post.postId.lt(cursor);
  }
}