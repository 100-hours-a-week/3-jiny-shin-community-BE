package com.jinyshin.ktbcommunity.domain.comment.dto;

import com.jinyshin.ktbcommunity.domain.comment.dto.response.CommentInfoResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CreatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.UpdatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import com.jinyshin.ktbcommunity.global.common.AuthorInfo;

public final class CommentMapper {

  private CommentMapper() {
  }

  public static CreatedCommentResponse toCreatedComment(Comment comment) {
    return new CreatedCommentResponse(
        comment.getCommentId(),
        comment.getContent(),
        toAuthorInfo(comment),
        comment.getCreatedAt()
    );
  }

  public static UpdatedCommentResponse toUpdatedComment(Comment comment) {
    return new UpdatedCommentResponse(
        comment.getCommentId(),
        comment.getContent(),
        comment.getUpdatedAt()
    );
  }

  public static CommentInfoResponse toCommentInfo(Comment comment, boolean isAuthor) {
    return new CommentInfoResponse(
        comment.getCommentId(),
        comment.getDisplayContent(),
        toAuthorInfo(comment),
        isAuthor,
        comment.isDeleted(),
        comment.getCreatedAt(),
        comment.getUpdatedAt()
    );
  }

  private static AuthorInfo toAuthorInfo(Comment comment) {
    return new AuthorInfo(
        comment.getAuthor().getUserId(),
        comment.getAuthor().getNickname(),
        comment.getAuthor().getProfileImage() != null
            ? comment.getAuthor().getProfileImage().getFilename()
            : null
    );
  }
}
