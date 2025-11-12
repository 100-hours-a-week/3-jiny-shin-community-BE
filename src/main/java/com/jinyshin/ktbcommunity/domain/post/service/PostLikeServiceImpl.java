package com.jinyshin.ktbcommunity.domain.post.service;

import com.jinyshin.ktbcommunity.domain.post.dto.PostMapper;
import com.jinyshin.ktbcommunity.domain.post.dto.response.LikeResponse;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.entity.PostLike;
import com.jinyshin.ktbcommunity.domain.post.repository.PostLikeRepository;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.ConflictException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeServiceImpl implements PostLikeService {

  private final PostLikeRepository postLikeRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public LikeResponse addLike(Long postId, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(ResourceNotFoundException::user);

    // PostStats만 fetch join: 좋아요 수 증가를 위해 필요
    Post post = postRepository.findByIdWithStats(postId)
        .orElseThrow(ResourceNotFoundException::post);

    // 중복 좋아요 방지 (UNIQUE 제약조건 이전 체크)
    if (postLikeRepository.existsByPostAndUser(post, user)) {
      throw ConflictException.postLikeAlreadyExists();
    }

    PostLike postLike = new PostLike(user, post);
    postLikeRepository.save(postLike);

    // 좋아요 추가 시 게시글의 좋아요 수 증가
    post.getPostStats().incrementLikeCount();

    return PostMapper.toLikeResponse(post, true);
  }

  @Override
  @Transactional
  public void removeLike(Long postId, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(ResourceNotFoundException::user);

    // PostStats만 fetch join: 좋아요 수 감소를 위해 필요
    Post post = postRepository.findByIdWithStats(postId)
        .orElseThrow(ResourceNotFoundException::post);

    PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
        .orElseThrow(ResourceNotFoundException::like);

    postLikeRepository.delete(postLike);

    // 좋아요 취소 시 게시글의 좋아요 수 감소
    post.getPostStats().decrementLikeCount();
  }
}