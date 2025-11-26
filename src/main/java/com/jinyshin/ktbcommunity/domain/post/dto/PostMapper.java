package com.jinyshin.ktbcommunity.domain.post.dto;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.POST_CONTENT_PREVIEW_LENGTH;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.LikeResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostImageResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostInfoResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import java.util.List;

public final class PostMapper {

  private PostMapper() {
  }

  public static CreatedPostResponse toCreatedPost(Post post, List<PostImageResponse> images) {
    return new CreatedPostResponse(
        post.getPostId(),
        post.getTitle(),
        post.getContent(),
        images,
        post.getCreatedAt()
    );
  }

  public static UpdatedPostResponse toUpdatedPost(Post post, List<PostImageResponse> images) {
    return new UpdatedPostResponse(
        post.getPostId(),
        post.getTitle(),
        post.getContent(),
        images,
        post.getUpdatedAt()
    );
  }

  public static PostInfoResponse toPostInfo(Post post, ImageUrlsResponse thumbnailUrls,
      ImageUrlsResponse profileImageUrls) {
    String contentPreview = post.getContent().length() > POST_CONTENT_PREVIEW_LENGTH
        ? post.getContent().substring(0, POST_CONTENT_PREVIEW_LENGTH)
        : post.getContent();

    return new PostInfoResponse(
        post.getPostId(),
        post.getTitle(),
        contentPreview,
        toAuthorInfo(post, profileImageUrls),
        thumbnailUrls,
        post.getCreatedAt(),
        post.getUpdatedAt(),
        post.getPostStats().getLikeCount(),
        post.getPostStats().getCommentCount(),
        post.getPostStats().getViewCount()
    );
  }

  public static PostDetailResponse toPostDetail(Post post, boolean isLiked, boolean isAuthor,
      List<PostImageResponse> images, ImageUrlsResponse profileImageUrls) {
    return new PostDetailResponse(
        post.getPostId(),
        post.getTitle(),
        post.getContent(),
        toAuthorInfo(post, profileImageUrls),
        post.getPostStats().getLikeCount(),
        post.getPostStats().getCommentCount(),
        post.getPostStats().getViewCount(),
        post.getCreatedAt(),
        post.getUpdatedAt(),
        isLiked,
        isAuthor,
        images
    );
  }

  private static AuthorInfo toAuthorInfo(Post post, ImageUrlsResponse profileImageUrls) {
    return new AuthorInfo(
        post.getAuthor().getUserId(),
        post.getAuthor().getNickname(),
        profileImageUrls
    );
  }

  public static LikeResponse toLikeResponse(Post post, boolean isLiked) {
    return new LikeResponse(
        post.getPostId(),
        post.getPostStats().getLikeCount(),
        isLiked
    );
  }
}