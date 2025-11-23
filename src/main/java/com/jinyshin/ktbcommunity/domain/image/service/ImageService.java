package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.processor.ImageProcessor;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {

  private final ImageRepository imageRepository;
  private final FileService fileService;
  private final ImageProcessor imageProcessor;

  /**
   * 이미지 업로드 (JPG 단일 포맷)
   */
  public Image uploadImage(MultipartFile file, ImageType imageType) {
    // 1. 이미지 처리 (JPG 압축)
    File processedFile = imageProcessor.processImage(file, imageType);

    // 2. 파일명 생성 (UUID 기반)
    String filename = generateFilename(imageType);

    try {
      // 3. S3에 파일 저장
      fileService.saveFile(processedFile, filename);

      // 4. DB에 이미지 메타정보 저장
      Image image = new Image(filename, imageType);
      Image savedImage = imageRepository.save(image);

      log.info("이미지 업로드 완료: {}", savedImage.getImageId());
      return savedImage;
    } finally {
      // 5. 임시 파일 정리
      cleanupTempFile(processedFile);
    }
  }

  @Transactional(readOnly = true)
  public Image getImage(Long imageId) {
    return imageRepository.findById(imageId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.IMAGE_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<Image> getImages(List<Long> imageIds) {
    return imageRepository.findByImageIdIn(imageIds);
  }

  public void deleteImage(Long imageId) {
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.IMAGE_NOT_FOUND));

    // Soft Delete (@SQLDelete가 자동 실행)
    imageRepository.delete(image);

    log.info("이미지 삭제 완료: {}", imageId);
  }

  /**
   * 이미지 물리 파일 삭제 (S3)
   */
  public void deletePhysicalFile(String filename) {
    fileService.deleteFile(filename);
  }

  /**
   * UUID 기반 파일명 생성 예: abc123_profile.jpg
   */
  private String generateFilename(ImageType imageType) {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    return uuid + "_" + imageType.getFileNameSuffix() + ".jpg";
  }

  private void cleanupTempFile(File file) {
    try {
      if (file != null && file.exists()) {
        Files.deleteIfExists(file.toPath());
      }
    } catch (IOException e) {
      log.warn("임시 파일 정리 실패: {}", file.getName(), e);
    }
  }
}
