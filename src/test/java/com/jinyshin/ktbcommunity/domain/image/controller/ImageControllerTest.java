package com.jinyshin.ktbcommunity.domain.image.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.service.FileService;
import com.jinyshin.ktbcommunity.domain.image.service.ImageService;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("ImageController 통합 테스트")
class ImageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ImageService imageService;

  @MockitoBean
  private FileService fileService;

  @Test
  @DisplayName("POST /api/images - 정상적인 이미지 업로드 시 201 Created와 S3 URL을 반환한다")
  void uploadImage_ValidImage_Returns201CreatedWithS3Url() throws Exception {
    // Given
    MockMultipartFile file = createMockImageFile("test.jpg", "image/jpeg");
    Image mockImage = new Image("test_profile.jpg", ImageType.PROFILE);
    String s3Url = "https://ktb-community-images.s3.ap-northeast-2.amazonaws.com/dev/profile/test_profile.jpg";

    given(imageService.uploadImage(any(), any())).willReturn(mockImage);
    given(fileService.getPublicUrl("test_profile.jpg")).willReturn(s3Url);

    // When & Then
    mockMvc.perform(multipart("/api/images")
            .file(file)
            .param("imageType", "PROFILE"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.filename").value("test_profile.jpg"))
        .andExpect(jsonPath("$.data.imageUrl").value(s3Url))
        .andExpect(jsonPath("$.data.imageType").value("PROFILE"));

    verify(imageService, times(1)).uploadImage(any(), any());
    verify(fileService, times(1)).getPublicUrl("test_profile.jpg");
  }

  @Test
  @DisplayName("POST /api/images - 빈 파일 업로드 시 400 Bad Request를 반환한다")
  void uploadImage_EmptyFile_Returns400BadRequest() throws Exception {
    // Given
    MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg",
        new byte[0]);

    given(imageService.uploadImage(any(), any()))
        .willThrow(new ApiException(ApiErrorCode.IMAGE_FILE_EMPTY));

    // When & Then
    mockMvc.perform(multipart("/api/images")
            .file(emptyFile)
            .param("imageType", "PROFILE"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("GET /api/images/{imageId} - 존재하는 이미지 조회 시 200 OK와 S3 URL을 반환한다")
  void getImage_ExistingImage_Returns200OKWithS3Url() throws Exception {
    // Given
    Long imageId = 1L;
    Image mockImage = createMockImage("test_profile.jpg", ImageType.PROFILE);
    String s3Url = "https://ktb-community-images.s3.ap-northeast-2.amazonaws.com/dev/profile/test_profile.jpg";

    given(imageService.getImage(imageId)).willReturn(mockImage);
    given(fileService.getPublicUrl("test_profile.jpg")).willReturn(s3Url);

    // When & Then
    mockMvc.perform(get("/api/images/{imageId}", imageId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("이미지 조회 성공"))
        .andExpect(jsonPath("$.data.filename").value("test_profile.jpg"))
        .andExpect(jsonPath("$.data.imageUrl").value(s3Url))
        .andExpect(jsonPath("$.data.imageType").value("PROFILE"));

    verify(imageService, times(1)).getImage(imageId);
    verify(fileService, times(1)).getPublicUrl("test_profile.jpg");
  }

  @Test
  @DisplayName("GET /api/images/{imageId} - 존재하지 않는 이미지 조회 시 404 Not Found를 반환한다")
  void getImage_NonExistingImage_Returns404NotFound() throws Exception {
    // Given
    Long imageId = 999L;

    given(imageService.getImage(imageId))
        .willThrow(new ApiException(ApiErrorCode.IMAGE_NOT_FOUND));

    // When & Then
    mockMvc.perform(get("/api/images/{imageId}", imageId))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());

    verify(imageService, times(1)).getImage(imageId);
  }

  @Test
  @DisplayName("GET /api/images - 여러 이미지 조회 시 200 OK와 S3 URL들을 반환한다")
  void getImages_MultipleImageIds_Returns200OKWithS3Urls() throws Exception {
    // Given
    List<Image> mockImages = List.of(
        createMockImage("image1_profile.jpg", ImageType.PROFILE),
        createMockImage("image2_post_content.jpg", ImageType.POST_CONTENTS),
        createMockImage("image3_post_thumbnail.jpg", ImageType.POST_THUMBNAIL)
    );

    given(imageService.getImages(anyList())).willReturn(mockImages);
    given(fileService.getPublicUrl("image1_profile.jpg"))
        .willReturn(
            "https://ktb-community-images.s3.ap-northeast-2.amazonaws.com/dev/profile/image1_profile.jpg");
    given(fileService.getPublicUrl("image2_post_content.jpg"))
        .willReturn(
            "https://ktb-community-images.s3.ap-northeast-2.amazonaws.com/dev/post_content/image2_post_content.jpg");
    given(fileService.getPublicUrl("image3_post_thumbnail.jpg"))
        .willReturn(
            "https://ktb-community-images.s3.ap-northeast-2.amazonaws.com/dev/post_thumbnail/image3_post_thumbnail.jpg");

    // When & Then
    mockMvc.perform(get("/api/images")
            .param("imageIds", "1", "2", "3"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("이미지 목록 조회 성공"))
        .andExpect(jsonPath("$.data.length()").value(3))
        .andExpect(jsonPath("$.data[0].filename").value("image1_profile.jpg"))
        .andExpect(jsonPath("$.data[1].filename").value("image2_post_content.jpg"))
        .andExpect(jsonPath("$.data[2].filename").value("image3_post_thumbnail.jpg"));

    verify(imageService, times(1)).getImages(anyList());
  }

  @Test
  @DisplayName("DELETE /api/images/{imageId} - 이미지 삭제 시 200 OK를 반환한다")
  void deleteImage_ExistingImage_Returns200OK() throws Exception {
    // Given
    Long imageId = 1L;
    doNothing().when(imageService).deleteImage(imageId);

    // When & Then
    mockMvc.perform(delete("/api/images/{imageId}", imageId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("이미지 삭제 성공"));

    verify(imageService, times(1)).deleteImage(imageId);
  }

  @Test
  @DisplayName("POST /api/images - 각 ImageType별로 올바른 응답을 반환한다")
  void uploadImage_DifferentImageTypes_ReturnsCorrectResponses() throws Exception {
    // Given - PROFILE
    MockMultipartFile profileFile = createMockImageFile("profile.jpg", "image/jpeg");
    Image profileImage = new Image("profile.jpg", ImageType.PROFILE);
    given(imageService.uploadImage(any(), any())).willReturn(profileImage);

    // When & Then - PROFILE
    mockMvc.perform(multipart("/api/images")
            .file(profileFile)
            .param("imageType", "PROFILE"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.imageType").value("PROFILE"));

    // Given - POST_CONTENTS
    MockMultipartFile contentFile = createMockImageFile("content.jpg", "image/jpeg");
    Image contentImage = new Image("content.jpg", ImageType.POST_CONTENTS);
    given(imageService.uploadImage(any(), any())).willReturn(contentImage);

    // When & Then - POST_CONTENTS
    mockMvc.perform(multipart("/api/images")
            .file(contentFile)
            .param("imageType", "POST_CONTENTS"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.imageType").value("POST_CONTENTS"));

    // Given - POST_THUMBNAIL
    MockMultipartFile thumbnailFile = createMockImageFile("thumbnail.jpg", "image/jpeg");
    Image thumbnailImage = new Image("thumbnail.jpg", ImageType.POST_THUMBNAIL);
    given(imageService.uploadImage(any(), any())).willReturn(thumbnailImage);

    // When & Then - POST_THUMBNAIL
    mockMvc.perform(multipart("/api/images")
            .file(thumbnailFile)
            .param("imageType", "POST_THUMBNAIL"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.imageType").value("POST_THUMBNAIL"));
  }

  /**
   * 테스트용 이미지 파일을 생성하는 헬퍼 메서드
   */
  private MockMultipartFile createMockImageFile(String filename, String contentType)
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

  /**
   * 테스트용 Image 객체를 생성하는 헬퍼 메서드 (createdAt 자동 설정)
   */
  private Image createMockImage(String filename, ImageType imageType) {
    Image image = new Image(filename, imageType);
    try {
      Field createdAtField = Image.class.getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(image, LocalDateTime.now());
    } catch (Exception e) {
      throw new RuntimeException("Failed to set createdAt for test Image", e);
    }
    return image;
  }
}