package com.jinyshin.ktbcommunity.domain.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ImageService 통합 테스트")
class ImageServiceTest {

  @Autowired
  private ImageService imageService;

  @MockitoBean
  private FileService fileService;

  @MockitoBean
  private ImageRepository imageRepository;

  @Test
  @DisplayName("정상적인 이미지 업로드 시 Image 엔티티가 저장된다")
  void uploadImage_ValidImage_SavesSuccessfully() throws IOException {
    // Given
    MultipartFile mockFile = createMockImageFile("test.jpg", "image/jpeg");
    ImageType imageType = ImageType.PROFILE;

    // FileService Mock 설정 (S3 Key 반환)
    given(fileService.saveFile(any(File.class), anyString()))
        .willReturn("dev/profile/abc123_profile.jpg");

    // ImageRepository Mock 설정
    Image savedImage = new Image("abc123_profile.jpg", imageType);
    given(imageRepository.save(any(Image.class))).willReturn(savedImage);

    // When
    Image result = imageService.uploadImage(mockFile, imageType);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFilename()).isEqualTo("abc123_profile.jpg");
    assertThat(result.getImageType()).isEqualTo(imageType);

    verify(fileService, times(1)).saveFile(any(File.class), anyString());
    verify(imageRepository, times(1)).save(any(Image.class));
  }

  @Test
  @DisplayName("빈 파일 업로드 시 예외가 발생한다")
  void uploadImage_EmptyFile_ThrowsException() {
    // Given
    MultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg",
        new byte[0]);
    ImageType imageType = ImageType.PROFILE;

    // When & Then
    assertThatThrownBy(() -> imageService.uploadImage(emptyFile, imageType))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_FILE_EMPTY);
  }

  @Test
  @DisplayName("존재하는 이미지 ID로 조회 시 Image 엔티티를 반환한다")
  void getImage_ExistingImageId_ReturnsImage() {
    // Given
    Long imageId = 1L;
    Image mockImage = new Image("test.jpg", ImageType.PROFILE);
    given(imageRepository.findById(imageId)).willReturn(Optional.of(mockImage));

    // When
    Image result = imageService.getImage(imageId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFilename()).isEqualTo("test.jpg");
    assertThat(result.getImageType()).isEqualTo(ImageType.PROFILE);

    verify(imageRepository, times(1)).findById(imageId);
  }

  @Test
  @DisplayName("존재하지 않는 이미지 ID로 조회 시 예외가 발생한다")
  void getImage_NonExistingImageId_ThrowsException() {
    // Given
    Long imageId = 999L;
    given(imageRepository.findById(imageId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> imageService.getImage(imageId))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_NOT_FOUND);

    verify(imageRepository, times(1)).findById(imageId);
  }

  @Test
  @DisplayName("여러 이미지 ID로 조회 시 Image 리스트를 반환한다")
  void getImages_MultipleImageIds_ReturnsImageList() {
    // Given
    List<Long> imageIds = List.of(1L, 2L, 3L);
    List<Image> mockImages = List.of(
        new Image("image1.jpg", ImageType.PROFILE),
        new Image("image2.jpg", ImageType.POST_CONTENTS),
        new Image("image3.jpg", ImageType.POST_THUMBNAIL)
    );
    given(imageRepository.findByImageIdIn(imageIds)).willReturn(mockImages);

    // When
    List<Image> result = imageService.getImages(imageIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getFilename()).isEqualTo("image1.jpg");
    assertThat(result.get(1).getFilename()).isEqualTo("image2.jpg");
    assertThat(result.get(2).getFilename()).isEqualTo("image3.jpg");

    verify(imageRepository, times(1)).findByImageIdIn(imageIds);
  }

  @Test
  @DisplayName("이미지 삭제 시 Repository delete가 호출된다")
  void deleteImage_ExistingImage_DeletesSuccessfully() {
    // Given
    Long imageId = 1L;
    Image mockImage = new Image("test.jpg", ImageType.PROFILE);
    given(imageRepository.findById(imageId)).willReturn(Optional.of(mockImage));

    // When
    imageService.deleteImage(imageId);

    // Then
    verify(imageRepository, times(1)).findById(imageId);
    verify(imageRepository, times(1)).delete(mockImage);
  }

  @Test
  @DisplayName("물리 파일 삭제 시 FileService deleteFile이 호출된다")
  void deletePhysicalFile_ValidFilename_CallsFileService() {
    // Given
    String filename = "test.jpg";

    // When
    imageService.deletePhysicalFile(filename);

    // Then
    verify(fileService, times(1)).deleteFile(filename);
  }

  /**
   * 테스트용 이미지 파일을 생성하는 헬퍼 메서드
   */
  private MultipartFile createMockImageFile(String filename, String contentType)
      throws IOException {
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(Color.RED);
    graphics.fillRect(0, 0, 100, 100);
    graphics.dispose();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", baos);
    byte[] imageBytes = baos.toByteArray();

    return new MockMultipartFile("file", filename, contentType, imageBytes);
  }
}