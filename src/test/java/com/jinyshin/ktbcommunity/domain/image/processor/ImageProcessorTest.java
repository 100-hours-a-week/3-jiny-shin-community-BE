package com.jinyshin.ktbcommunity.domain.image.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("ImageProcessor 단위 테스트")
class ImageProcessorTest {

  private final ImageProcessor imageProcessor = new ImageProcessor();
  private File createdJpgFile;

  @AfterEach
  void cleanup() {
    // 테스트 후 생성된 임시 파일 삭제
    if (createdJpgFile != null && createdJpgFile.exists()) {
      createdJpgFile.delete();
    }
  }

  @Test
  @DisplayName("정상적인 이미지 파일을 처리하면 JPG 파일이 생성된다")
  void processImage_ValidImage_CreatesJpgFile() throws IOException {
    // Given
    MultipartFile mockFile = createMockImageFile("test-image.jpg", "image/jpeg");

    // When
    File result = imageProcessor.processImage(mockFile, ImageType.PROFILE);

    // Then
    createdJpgFile = result;

    assertThat(createdJpgFile).isNotNull().exists();
    assertThat(createdJpgFile.getName()).contains("profile").endsWith(".jpg");
  }

  @Test
  @DisplayName("빈 파일을 업로드하면 IMAGE_FILE_EMPTY 예외가 발생한다")
  void validateFile_EmptyFile_ThrowsException() {
    // Given
    MultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg",
        new byte[0]);

    // When & Then
    assertThatThrownBy(() -> imageProcessor.processImage(emptyFile, ImageType.PROFILE))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_FILE_EMPTY);
  }

  @Test
  @DisplayName("10MB를 초과하는 파일을 업로드하면 IMAGE_FILE_TOO_LARGE 예외가 발생한다")
  void validateFile_FileTooLarge_ThrowsException() {
    // Given
    byte[] largeFileContent = new byte[11 * 1024 * 1024]; // 11MB
    MultipartFile largeFile = new MockMultipartFile("file", "large.jpg", "image/jpeg",
        largeFileContent);

    // When & Then
    assertThatThrownBy(() -> imageProcessor.processImage(largeFile, ImageType.PROFILE))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_FILE_TOO_LARGE);
  }

  @Test
  @DisplayName("허용되지 않은 포맷의 파일을 업로드하면 IMAGE_INVALID_FORMAT 예외가 발생한다")
  void validateFile_InvalidFormat_ThrowsException() {
    // Given
    MultipartFile invalidFile = new MockMultipartFile("file", "document.exe",
        "application/x-msdownload", "fake content".getBytes());

    // When & Then
    assertThatThrownBy(() -> imageProcessor.processImage(invalidFile, ImageType.PROFILE))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_INVALID_FORMAT);
  }

  @Test
  @DisplayName("ImageType에 따라 올바른 postfix가 파일명에 포함된다")
  void processImage_DifferentImageTypes_UsesCorrectPostfix() throws IOException {
    // Given
    MultipartFile mockFile = createMockImageFile("test.jpg", "image/jpeg");

    // When
    File profileResult = imageProcessor.processImage(mockFile,
        ImageType.PROFILE);
    File contentResult = imageProcessor.processImage(mockFile,
        ImageType.POST_CONTENTS);
    File thumbnailResult = imageProcessor.processImage(mockFile,
        ImageType.POST_THUMBNAIL);

    // Then
    assertThat(profileResult.getName()).contains("profile");
    assertThat(contentResult.getName()).contains("post_content");
    assertThat(thumbnailResult.getName()).contains("post_thumbnail");

    // Cleanup
    profileResult.delete();
    contentResult.delete();
    thumbnailResult.delete();
  }

  /**
   * 테스트용 이미지 파일을 생성하는 헬퍼 메서드
   */
  private MultipartFile createMockImageFile(String filename, String contentType)
      throws IOException {
    // 100x100 픽셀의 빨간색 이미지 생성
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(Color.RED);
    graphics.fillRect(0, 0, 100, 100);
    graphics.dispose();

    // BufferedImage를 byte[]로 변환
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", baos);
    byte[] imageBytes = baos.toByteArray();

    return new MockMultipartFile("file", filename, contentType, imageBytes);
  }
}