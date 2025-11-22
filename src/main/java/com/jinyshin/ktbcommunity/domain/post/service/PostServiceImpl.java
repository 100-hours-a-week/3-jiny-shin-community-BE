package com.jinyshin.ktbcommunity.domain.post.service;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.DEFAULT_PAGE_LIMIT;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.MAX_PAGE_LIMIT;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.domain.post.dto.PostMapper;
import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.request.UpdatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostInfoResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostListResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.repository.PostLikeRepository;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.BadRequestException;
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
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final ImageRepository imageRepository;
  private final PostLikeRepository postLikeRepository;

  @Override
  @Transactional
  public CreatedPostResponse createPost(Long userId, CreatePostRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(ResourceNotFoundException::user);

    Post post = new Post(request.title(), request.content(), user);

    if (request.imageIds() != null && !request.imageIds().isEmpty()) {
      // primaryImageId 검증
      if (request.primaryImageId() != null && !request.imageIds()
          .contains(request.primaryImageId())) {
        throw BadRequestException.primaryImageNotInList();
      }

      List<Image> images = imageRepository.findByImageIdIn(request.imageIds());
      if (images.size() != request.imageIds().size()) {
        throw ResourceNotFoundException.image();
      }

      for (int i = 0; i < images.size(); i++) {
        post.addImage(images.get(i), i + 1);
      }

      if (request.primaryImageId() != null) {
        // 사용자가 선택한 대표 이미지 설정
        post.getPostImages().stream()
            .filter(postImage -> postImage.getImage().getImageId().equals(request.primaryImageId()))
            .findFirst()
            .ifPresent(postImage -> postImage.setPrimary(true));
      } else {
        // 대표 이미지가 없다면 첫 번째 이미지를 대표로 설정
        post.getPostImages().stream()
            .filter(postImage -> postImage.getPosition() == 1)
            .findFirst()
            .ifPresent(postImage -> postImage.setPrimary(true));
      }
    }

    Post savedPost = postRepository.save(post);
    return PostMapper.toCreatedPost(savedPost);
  }

  @Override
  @Transactional(readOnly = true)
  public PostListResponse getPosts(Long cursor, String sort, int limit, Long currentUserId) {
    if (limit <= 0 || limit > MAX_PAGE_LIMIT) {
      limit = DEFAULT_PAGE_LIMIT;
    }

    if (sort == null || (!sort.equals("asc") && !sort.equals("desc"))) {
      sort = "desc";
    }

    // limit+1 조회: hasNext 판단을 위해 1개 더 조회
    List<Post> posts = postRepository.findPostsWithCursor(cursor, sort, limit);

    boolean hasNext = posts.size() > limit;
    if (hasNext) {
      posts.removeLast();  // 초과분 제거
    }

    Long nextCursor = hasNext && !posts.isEmpty()
        ? posts.getLast().getPostId()
        : null;

    List<PostInfoResponse> postInfos = posts.stream()
        .map(PostMapper::toPostInfo)
        .collect(Collectors.toList());

    return new PostListResponse(hasNext, nextCursor, postInfos);
  }

  @Override
  @Transactional
  public PostDetailResponse getPostDetail(Long postId, Long currentUserId) {
    // N+1 방지: author, profileImage, postStats, postImages를 한 번에 로딩
    Post post = postRepository.findByIdWithDetails(postId)
        .orElseThrow(ResourceNotFoundException::post);

    post.getPostStats().incrementViewCount();

    boolean isLiked = false;
    boolean isAuthor = false;

    if (currentUserId != null) {
      // User 조회 없이 ID만으로 체크 (성능 최적화)
      isLiked = postLikeRepository.existsByPostPostIdAndUserUserId(postId, currentUserId);
      isAuthor = post.getAuthor().getUserId().equals(currentUserId);
    }

    return PostMapper.toPostDetail(post, isLiked, isAuthor);
  }

  @Override
  @Transactional
  public UpdatedPostResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
    if (request.title() == null && request.content() == null && request.imageIds() == null) {
      throw BadRequestException.noFieldsToUpdate();
    }

    Post post = postRepository.findById(postId)
        .orElseThrow(ResourceNotFoundException::post);

    if (!post.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    post.update(request.title(), request.content());

    if (request.imageIds() != null) {
      post.clearImages();

      if (!request.imageIds().isEmpty()) {
        List<Image> images = imageRepository.findByImageIdIn(request.imageIds());
        if (images.size() != request.imageIds().size()) {
          throw ResourceNotFoundException.image();
        }

        for (int i = 0; i < images.size(); i++) {
          post.addImage(images.get(i), i);
        }
      }
    }

    return PostMapper.toUpdatedPost(post);
  }

  @Override
  @Transactional
  public void deletePost(Long postId, Long userId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(ResourceNotFoundException::post);

    if (!post.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    postRepository.delete(post);
  }
}
