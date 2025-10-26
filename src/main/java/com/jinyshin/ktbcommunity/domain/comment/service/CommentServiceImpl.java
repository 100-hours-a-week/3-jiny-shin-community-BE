package com.jinyshin.ktbcommunity.domain.comment.service;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.DEFAULT_PAGE_LIMIT;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.MAX_PAGE_LIMIT;

import com.jinyshin.ktbcommunity.domain.comment.dto.CommentMapper;
import com.jinyshin.ktbcommunity.domain.comment.dto.request.CreateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.request.UpdateCommentRequest;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CommentInfoResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CommentListResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.CreatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.dto.response.UpdatedCommentResponse;
import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import com.jinyshin.ktbcommunity.domain.comment.repository.CommentRepository;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public CreatedCommentResponse createComment(Long postId, Long userId,
      CreateCommentRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(ResourceNotFoundException::user);

    // PostStats만 fetch join: 댓글 수 증가를 위해 필요
    Post post = postRepository.findByIdWithStats(postId)
        .orElseThrow(ResourceNotFoundException::post);

    Comment comment = new Comment(request.contents(), user, post);
    Comment savedComment = commentRepository.save(comment);

    // 댓글 추가 시 게시글의 댓글 수 증가
    post.getPostStats().incrementCommentCount();

    return CommentMapper.toCreatedComment(savedComment);
  }

  @Override
  @Transactional(readOnly = true)
  public CommentListResponse getComments(Long postId, Long cursor, String sort, int limit,
      Long currentUserId) {
    if (limit <= 0 || limit > MAX_PAGE_LIMIT) {
      limit = DEFAULT_PAGE_LIMIT;
    }

    if (sort == null || (!sort.equals("asc") && !sort.equals("desc"))) {
      sort = "desc";
    }

    List<Comment> comments = commentRepository.findCommentsWithCursor(postId, cursor, sort, limit);

    boolean hasNext = comments.size() > limit;
    if (hasNext) {
      comments.removeLast();
    }

    Long nextCursor = hasNext && !comments.isEmpty()
        ? comments.getLast().getCommentId()
        : null;

    List<CommentInfoResponse> commentInfos = comments.stream()
        .map(comment -> {
          boolean isAuthor = comment.getAuthor().getUserId().equals(currentUserId);
          return CommentMapper.toCommentInfo(comment, isAuthor);
        })
        .collect(Collectors.toList());

    return new CommentListResponse(hasNext, nextCursor, comments.size(), commentInfos);
  }

  @Override
  @Transactional
  public UpdatedCommentResponse updateComment(Long commentId, Long userId,
      UpdateCommentRequest request) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(ResourceNotFoundException::comment);

    if (!comment.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    comment.update(request.contents());

    return CommentMapper.toUpdatedComment(comment);
  }

  @Override
  @Transactional
  public void deleteComment(Long commentId, Long userId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(ResourceNotFoundException::comment);

    if (!comment.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    // Soft delete이지만 목록에는 표시되므로 댓글 수는 유지
    // (삭제된 댓글도 "삭제된 댓글입니다"로 표시됨)
    commentRepository.delete(comment);
  }
}
