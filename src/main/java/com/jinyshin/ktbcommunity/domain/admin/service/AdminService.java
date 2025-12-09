package com.jinyshin.ktbcommunity.domain.admin.service;

import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import com.jinyshin.ktbcommunity.domain.comment.repository.CommentRepository;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.entity.PostImage;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

  private static final Set<Long> ADMIN_USER_IDS = Set.of(1L, 2L);
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  private boolean isAdmin(Long userId) {
    return userId != null && ADMIN_USER_IDS.contains(userId);
  }

  private void validateAdminAccess(Long userId) {
    if (!isAdmin(userId)) {
      throw ForbiddenException.forbidden();
    }
  }

  @Transactional
  public void deletePostByAdmin(Long postId, Long adminUserId) {
    validateAdminAccess(adminUserId);

    Post post = postRepository.findById(postId)
        .orElseThrow(ResourceNotFoundException::post);

    post.getPostImages().stream()
        .map(PostImage::getImage)
        .forEach(Image::markAsDeleted);

    postRepository.delete(post);
  }

  @Transactional
  public void deleteCommentByAdmin(Long commentId, Long adminUserId) {
    validateAdminAccess(adminUserId);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(ResourceNotFoundException::comment);

    commentRepository.delete(comment);
  }
}