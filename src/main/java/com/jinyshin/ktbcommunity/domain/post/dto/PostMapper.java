package com.jinyshin.ktbcommunity.domain.post.dto;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_CONTENT_PREVIEW_LENGTH;

import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostInfoResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.global.common.AuthorInfo;

public final class PostMapper {

  private PostMapper() {
  }

  public static CreatedPostResponse toCreatedPost(Post post) {
    return new CreatedPostResponse(
        post.getPostId(),
        post.getTitle(),
        post.getCreatedAt()
    );
  }

  public static UpdatedPostResponse toUpdatedPost(Post post) {
    return new UpdatedPostResponse(
        post.getPostId(),
        post.getTitle(),
        post.getUpdatedAt()
    );
  }

  public static PostInfoResponse toPostInfo(Post post) {
    String contentPreview = post.getContent().length() > POST_CONTENT_PREVIEW_LENGTH
        ? post.getContent().substring(0, POST_CONTENT_PREVIEW_LENGTH)
        : post.getContent();

    return new PostInfoResponse(
        post.getPostId(),
        post.getTitle(),
        contentPreview,
        toAuthorInfo(post),
        post.getCreatedAt(),
        post.getUpdatedAt(),
        post.getPostStats().getViewCount()
    );
  }

  public static PostDetailResponse toPostDetail(Post post, boolean isAuthor) {
    return new PostDetailResponse(
        post.getPostId(),
        post.getTitle(),
        post.getContent(),
        toAuthorInfo(post),
        post.getPostStats().getViewCount(),
        post.getCreatedAt(),
        post.getUpdatedAt(),
        isAuthor
    );
  }

  private static AuthorInfo toAuthorInfo(Post post) {
    return new AuthorInfo(
        post.getAuthor().getUserId(),
        post.getAuthor().getNickname()
    );
  }
}