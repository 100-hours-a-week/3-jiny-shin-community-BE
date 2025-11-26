package com.jinyshin.ktbcommunity.domain.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageStatus;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.domain.post.dto.request.CreatePostRequest;
import com.jinyshin.ktbcommunity.domain.post.dto.response.PostDetailResponse;
import com.jinyshin.ktbcommunity.domain.post.service.PostService;
import com.jinyshin.ktbcommunity.domain.user.dto.request.ProfileUpdateRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.request.SignupRequest;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import com.jinyshin.ktbcommunity.domain.user.service.UserService;
import com.jinyshin.ktbcommunity.global.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImageLifecycleIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private PostService postService;

  @Autowired
  private ImageRepository imageRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("프로필 이미지 생명주기: TEMP → ACTIVE → DELETED")
  void profileImageLifecycle() {
    // Given: TEMP 상태의 프로필 이미지 생성
    Image profileImage = createTempImage(ImageType.PROFILE);
    imageRepository.save(profileImage);

    // When: 회원가입 시 프로필 이미지 연결
    SignupRequest signupRequest = new SignupRequest(
        "test@example.com",
        "testuser",
        "password123!",
        profileImage.getImageId()
    );
    UserInfoResponse userInfo = userService.signup(signupRequest);

    // Then: 이미지가 ACTIVE 상태로 변경되고 URL이 반환됨
    Image activatedImage = imageRepository.findById(profileImage.getImageId()).orElseThrow();
    assertThat(activatedImage.getStatus()).isEqualTo(ImageStatus.ACTIVE);
    assertThat(userInfo.profileImageUrls()).isNotNull();
    assertThat(userInfo.profileImageUrls().jpgUrl()).contains(activatedImage.getStoredFilename());

    // When: 새 프로필 이미지로 변경
    Image newProfileImage = createTempImage(ImageType.PROFILE);
    imageRepository.save(newProfileImage);

    ProfileUpdateRequest updateRequest = new ProfileUpdateRequest(
        "newNickname",
        newProfileImage.getImageId()
    );
    UpdatedProfileResponse updatedProfile = userService.updateProfile(
        userInfo.userId(),
        updateRequest
    );

    // Then: 기존 이미지는 DELETED, 새 이미지는 ACTIVE
    Image oldImage = imageRepository.findById(profileImage.getImageId()).orElseThrow();
    Image newImage = imageRepository.findById(newProfileImage.getImageId()).orElseThrow();

    assertThat(oldImage.getStatus()).isEqualTo(ImageStatus.DELETED);
    assertThat(oldImage.getDeletedAt()).isNotNull();
    assertThat(newImage.getStatus()).isEqualTo(ImageStatus.ACTIVE);
    assertThat(updatedProfile.profileImageUrls().jpgUrl()).contains(
        newImage.getStoredFilename());
  }

  @Test
  @DisplayName("게시물 이미지 생명주기: TEMP → ACTIVE (대표 이미지 자동 선택)")
  void postImageLifecycle_autoPrimarySelection() {
    // Given: 사용자 생성
    User user = createUser();
    userRepository.save(user);

    // TEMP 상태의 게시물 이미지 3개 생성
    Image image1 = createTempImage(ImageType.POST);
    Image image2 = createTempImage(ImageType.POST);
    Image image3 = createTempImage(ImageType.POST);
    imageRepository.saveAll(List.of(image1, image2, image3));

    // When: 대표 이미지를 지정하지 않고 게시물 생성 (첫 번째가 자동 선택되어야 함)
    CreatePostRequest request = new CreatePostRequest(
        "Test Title",
        "Test Content",
        List.of(image1.getImageId(), image2.getImageId(), image3.getImageId()),
        null // primaryImageId 없음
    );
    Long postId = postService.createPost(user.getUserId(), request).postId();

    // Then: 첫 번째 이미지가 대표 이미지로 설정됨
    PostDetailResponse postDetail = postService.getPostDetail(postId, user.getUserId());
    assertThat(postDetail.images()).hasSize(3);
    assertThat(postDetail.images().get(0).isPrimary()).isTrue();
    assertThat(postDetail.images().get(1).isPrimary()).isFalse();
    assertThat(postDetail.images().get(2).isPrimary()).isFalse();

    // 모든 이미지가 ACTIVE 상태
    List<Image> images = imageRepository.findAllById(
        List.of(image1.getImageId(), image2.getImageId(), image3.getImageId()));
    assertThat(images).allMatch(img -> img.getStatus() == ImageStatus.ACTIVE);
  }

  @Test
  @DisplayName("게시물 이미지 생명주기: 대표 이미지 명시적 선택")
  void postImageLifecycle_explicitPrimarySelection() {
    // Given: 사용자 생성
    User user = createUser();
    userRepository.save(user);

    // TEMP 상태의 게시물 이미지 3개 생성
    Image image1 = createTempImage(ImageType.POST);
    Image image2 = createTempImage(ImageType.POST);
    Image image3 = createTempImage(ImageType.POST);
    imageRepository.saveAll(List.of(image1, image2, image3));

    // When: 두 번째 이미지를 대표 이미지로 지정
    CreatePostRequest request = new CreatePostRequest(
        "Test Title",
        "Test Content",
        List.of(image1.getImageId(), image2.getImageId(), image3.getImageId()),
        image2.getImageId() // 두 번째 이미지를 대표로 선택
    );
    Long postId = postService.createPost(user.getUserId(), request).postId();

    // Then: 두 번째 이미지가 대표 이미지로 설정됨
    PostDetailResponse postDetail = postService.getPostDetail(postId, user.getUserId());
    assertThat(postDetail.images()).hasSize(3);

    // 두 번째 이미지(image2)가 대표 이미지
    assertThat(postDetail.images().stream()
        .filter(img -> img.imageId().equals(image2.getImageId()))
        .findFirst()
        .orElseThrow()
        .isPrimary()).isTrue();
  }

  @Test
  @DisplayName("만료된 이미지(1시간 경과) 사용 시 예외 발생")
  void expiredImageValidation() throws Exception {
    // Given: 1시간 이상 경과한 TEMP 이미지 생성
    Image expiredImage = createTempImage(ImageType.POST);
    imageRepository.save(expiredImage);

    // Reflection을 사용하여 createdAt을 2시간 전으로 설정
    java.lang.reflect.Field createdAtField = Image.class.getDeclaredField("createdAt");
    createdAtField.setAccessible(true);
    createdAtField.set(expiredImage, LocalDateTime.now().minusHours(2));
    imageRepository.flush();

    User user = createUser();
    userRepository.save(user);

    // When & Then: 만료된 이미지로 게시물 생성 시 예외 발생
    CreatePostRequest request = new CreatePostRequest(
        "Test Title",
        "Test Content",
        List.of(expiredImage.getImageId()),
        null
    );

    assertThatThrownBy(() -> postService.createPost(user.getUserId(), request))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  @DisplayName("ACTIVE 상태 이미지 재사용 시도 시 예외 발생")
  void activeImageReuse() {
    // Given: 이미 ACTIVE 상태인 이미지
    Image activeImage = createTempImage(ImageType.POST);
    activeImage.markAsActive();
    imageRepository.save(activeImage);

    User user = createUser();
    userRepository.save(user);

    // When & Then: ACTIVE 이미지를 다시 사용하려고 하면 예외 발생
    CreatePostRequest request = new CreatePostRequest(
        "Test Title",
        "Test Content",
        List.of(activeImage.getImageId()),
        null
    );

    assertThatThrownBy(() -> postService.createPost(user.getUserId(), request))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  @DisplayName("게시물 삭제 시 첨부 이미지 Soft Delete")
  void postDeletion_softDeleteImages() {
    // Given: 게시물과 이미지 생성
    User user = createUser();
    userRepository.save(user);

    Image image1 = createTempImage(ImageType.POST);
    Image image2 = createTempImage(ImageType.POST);
    imageRepository.saveAll(List.of(image1, image2));

    CreatePostRequest request = new CreatePostRequest(
        "Test Title",
        "Test Content",
        List.of(image1.getImageId(), image2.getImageId()),
        null
    );
    Long postId = postService.createPost(user.getUserId(), request).postId();

    // When: 게시물 삭제
    postService.deletePost(postId, user.getUserId());

    // Then: 이미지들이 DELETED 상태로 변경
    List<Image> images = imageRepository.findAllById(
        List.of(image1.getImageId(), image2.getImageId()));
    assertThat(images).allMatch(img -> img.getStatus() == ImageStatus.DELETED);
    assertThat(images).allMatch(img -> img.getDeletedAt() != null);
  }

  private Image createTempImage(ImageType imageType) {
    String uniqueId = String.valueOf(System.nanoTime());
    return new Image(
        "stored_" + uniqueId + ".jpg",
        "jpg",
        imageType == ImageType.PROFILE ? "profile/" : "post/",
        imageType
    );
  }

  private User createUser() {
    String uniqueId = String.valueOf(System.nanoTime());
    return new User(
        "test" + uniqueId + "@example.com",
        "testuser" + uniqueId,
        "hashedpassword"
    );
  }
}