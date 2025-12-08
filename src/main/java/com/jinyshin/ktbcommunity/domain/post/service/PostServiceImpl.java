package com.jinyshin.ktbcommunity.domain.post.service;

import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.DEFAULT_PAGE_LIMIT;
import static com.jinyshin.ktbcommunity.global.constants.ValidationConstants.MAX_PAGE_LIMIT;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageStatus;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.domain.image.util.ImageUrlGenerator;
import com.jinyshin.ktbcommunity.domain.post.dto.PostMapper;
import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.request.UpdatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.CreatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostImageResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostInfoResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostListResponse;
import com.jinyshin.ktbcommunity.domain.post.dto.response.UpdatedPostResponse;
import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.entity.PostImage;
import com.jinyshin.ktbcommunity.domain.post.repository.PostLikeRepository;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.global.exception.BadRequestException;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
  private final ImageUrlGenerator imageUrlGenerator;

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
        throw BadRequestException.primaryImageNotInImageIds();
      }

      // 이미지 검증 및 활성화
      List<Image> images = validateAndActivateImages(request.imageIds());

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

    // 게시글 이미지 목록 변환
    List<PostImageResponse> images = savedPost.getPostImages().stream()
        .map(postImage -> {
          ImageUrlsResponse urls = imageUrlGenerator.generatePostUrls(
              postImage.getImage(), false);
          return new PostImageResponse(
              postImage.getImage().getImageId(),
              urls,
              postImage.getPosition(),
              postImage.isPrimary()
          );
        })
        .toList();

    return PostMapper.toCreatedPost(savedPost, images);
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
        .map(post -> {
          // 썸네일 이미지 URL 생성
          PostImage thumbnailImage = post.getPostImages().stream()
              .filter(PostImage::isPrimary)
              .findFirst()
              .orElse(null);

          ImageUrlsResponse thumbnailUrls = thumbnailImage != null
              ? imageUrlGenerator.generatePostUrls(thumbnailImage.getImage(), true)
              : null;

          // 프로필 이미지 URL 생성
          ImageUrlsResponse profileImageUrls = post.getAuthor().getProfileImage() != null
              ? imageUrlGenerator.generateProfileUrls(post.getAuthor().getProfileImage())
              : null;

          return PostMapper.toPostInfo(post, thumbnailUrls, profileImageUrls);
        })
        .collect(Collectors.toList());

    return new PostListResponse(hasNext, nextCursor, postInfos.size(), postInfos);
  }

  @Override
  @Transactional(readOnly = true)
  public PostListResponse getMyPosts(Long userId, Long cursor, String sort, int limit) {
    if (limit <= 0 || limit > MAX_PAGE_LIMIT) {
      limit = DEFAULT_PAGE_LIMIT;
    }

    if (sort == null || (!sort.equals("asc") && !sort.equals("desc"))) {
      sort = "desc";
    }

    // limit+1 조회: hasNext 판단을 위해 1개 더 조회
    List<Post> posts = postRepository.findMyPostsWithCursor(userId, cursor, sort, limit);

    boolean hasNext = posts.size() > limit;
    if (hasNext) {
      posts.removeLast();
    }

    Long nextCursor = hasNext && !posts.isEmpty()
        ? posts.getLast().getPostId()
        : null;

    List<PostInfoResponse> postInfos = posts.stream()
        .map(post -> {
          // 썸네일 이미지 URL 생성
          PostImage thumbnailImage = post.getPostImages().stream()
              .filter(PostImage::isPrimary)
              .findFirst()
              .orElse(null);

          ImageUrlsResponse thumbnailUrls = thumbnailImage != null
              ? imageUrlGenerator.generatePostUrls(thumbnailImage.getImage(), true)
              : null;

          // 프로필 이미지 URL 생성
          ImageUrlsResponse profileImageUrls = post.getAuthor().getProfileImage() != null
              ? imageUrlGenerator.generateProfileUrls(post.getAuthor().getProfileImage())
              : null;

          return PostMapper.toPostInfo(post, thumbnailUrls, profileImageUrls);
        })
        .collect(Collectors.toList());

    return new PostListResponse(hasNext, nextCursor, postInfos.size(), postInfos);
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

    // 게시글 이미지 목록 변환
    List<PostImageResponse> images = post.getPostImages().stream()
        .map(postImage -> {
          ImageUrlsResponse urls = imageUrlGenerator.generatePostUrls(
              postImage.getImage(), false);
          return new PostImageResponse(
              postImage.getImage().getImageId(),
              urls,
              postImage.getPosition(),
              postImage.isPrimary()
          );
        })
        .toList();

    // 프로필 이미지 URL 생성
    ImageUrlsResponse profileImageUrls = post.getAuthor().getProfileImage() != null
        ? imageUrlGenerator.generateProfileUrls(post.getAuthor().getProfileImage())
        : null;

    return PostMapper.toPostDetail(post, isLiked, isAuthor, images, profileImageUrls);
  }

  @Override
  @Transactional
  public UpdatedPostResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
    Post post = postRepository.findById(postId)
        .orElseThrow(ResourceNotFoundException::post);

    if (!post.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    post.update(request.title(), request.content());

    // imageIds가 null이고 primaryImageId만 있는 경우: 대표 이미지만 변경
    if (request.imageIds() == null && request.primaryImageId() != null) {
      // 기존 게시글의 이미지 중에 primaryImageId가 있는지 확인
      boolean exists = post.getPostImages().stream()
          .anyMatch(postImage -> postImage.getImage().getImageId()
              .equals(request.primaryImageId()));

      if (!exists) {
        throw BadRequestException.primaryImageNotFoundInPost();
      }

      // 모든 이미지의 isPrimary를 false로 설정
      post.getPostImages().forEach(postImage -> postImage.setPrimary(false));

      // 새 대표 이미지 설정
      post.getPostImages().stream()
          .filter(postImage -> postImage.getImage().getImageId()
              .equals(request.primaryImageId()))
          .findFirst()
          .ifPresent(postImage -> postImage.setPrimary(true));
    }

    if (request.imageIds() != null) {
      // primaryImageId 검증
      if (request.primaryImageId() != null &&
          !request.imageIds().contains(request.primaryImageId())) {
        throw BadRequestException.primaryImageNotInImageIds();
      }

      // 기존 이미지들 Soft Delete
      post.getPostImages().stream()
          .map(PostImage::getImage)
          .forEach(Image::markAsDeleted);

      post.clearImages();

      if (!request.imageIds().isEmpty()) {
        // 이미지 검증 및 활성화
        List<Image> images = validateAndActivateImages(request.imageIds());

        // isPrimary 중복 방지: 먼저 모든 이미지를 false로 설정
        for (int i = 0; i < images.size(); i++) {
          post.addImage(images.get(i), i + 1);
        }

        // 대표 이미지 설정
        if (request.primaryImageId() != null) {
          post.getPostImages().stream()
              .filter(
                  postImage -> postImage.getImage().getImageId().equals(request.primaryImageId()))
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
    }

    // 게시글 이미지 목록 변환
    List<PostImageResponse> images = post.getPostImages().stream()
        .map(postImage -> {
          ImageUrlsResponse urls = imageUrlGenerator.generatePostUrls(
              postImage.getImage(), false);
          return new PostImageResponse(
              postImage.getImage().getImageId(),
              urls,
              postImage.getPosition(),
              postImage.isPrimary()
          );
        })
        .toList();

    return PostMapper.toUpdatedPost(post, images);
  }

  @Override
  @Transactional
  public void deletePost(Long postId, Long userId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(ResourceNotFoundException::post);

    if (!post.getAuthor().getUserId().equals(userId)) {
      throw ForbiddenException.forbidden();
    }

    // 모든 첨부 이미지 Soft Delete
    post.getPostImages().stream()
        .map(PostImage::getImage)
        .forEach(Image::markAsDeleted);

    postRepository.delete(post);
  }

  private List<Image> validateAndActivateImages(List<Long> imageIds) {
    List<Image> images = imageRepository.findByImageIdIn(imageIds);

    // 이미지 개수 확인
    if (images.size() != imageIds.size()) {
      throw ResourceNotFoundException.image();
    }

    // 각 이미지 검증 및 활성화
    for (Image image : images) {
      // TEMP 상태 확인
      if (image.getStatus() != ImageStatus.TEMP) {
        throw BadRequestException.imageAlreadyUsed();
      }

      // 1시간 만료 확인
      if (image.isExpired(1)) {
        throw BadRequestException.imageExpired();
      }

      // ACTIVE 상태로 변경
      image.markAsActive();
    }

    // DB IN 절은 순서를 보장하지 않으므로, 요청 순서대로 재정렬
    Map<Long, Image> imageMap =
        images.stream().collect(Collectors.toMap(Image::getImageId, Function.identity()));

    return imageIds.stream().map(imageMap::get).toList();
  }
}
