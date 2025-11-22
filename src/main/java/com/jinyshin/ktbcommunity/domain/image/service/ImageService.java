package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.domain.image.dto.ProcessedFiles;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.processor.ImageProcessor;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private final ImageProcessor imageProcessor;
  private final FileService fileService;
  private final ImageRepository imageRepository;

  /**
   * 이미지 업로드 및 저장
   *
   * @param file      업로드할 이미지 파일
   * @param imageType 이미지 타입 (PROFILE, POST_CONTENTS, POST_THUMBNAIL)
   * @return 저장된 Image 엔티티
   */
  @Transactional
  public Image uploadImage(MultipartFile file, ImageType imageType) {
    // 1. 이미지 처리 (포맷 변환 + 압축)
    ProcessedFiles processedFiles = imageProcessor.processImage(file, imageType);

    // 2. 파일 저장
    String savedPath = fileService.saveFile(processedFiles.getJpgFile(),
        processedFiles.getJpgFile().getName());

    // 3. DB에 메타데이터 저장
    Image image = new Image(processedFiles.getJpgFile().getName(), imageType);
    Image savedImage = imageRepository.save(image);

    log.info("이미지 업로드 완료 - ID: {}, Filename: {}", savedImage.getImageId(),
        savedImage.getFilename());

    return savedImage;
  }

  /**
   * 이미지 조회
   *
   * @param imageId 이미지 ID
   * @return Image 엔티티
   */
  @Transactional(readOnly = true)
  public Image getImage(Long imageId) {
    return imageRepository.findById(imageId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED));
  }

  /**
   * 여러 이미지 조회
   *
   * @param imageIds 이미지 ID 목록
   * @return Image 엔티티 리스트
   */
  @Transactional(readOnly = true)
  public List<Image> getImages(List<Long> imageIds) {
    return imageRepository.findByImageIdIn(imageIds);
  }

  /**
   * 이미지 삭제 (Soft Delete)
   *
   * @param imageId 이미지 ID
   */
  @Transactional
  public void deleteImage(Long imageId) {
    Image image = getImage(imageId);

    // Soft delete: @SQLDelete 어노테이션으로 자동 처리
    imageRepository.delete(image);

    log.info("이미지 삭제 완료 - ID: {}, Filename: {}", imageId, image.getFilename());
  }

  /**
   * 이미지 물리 파일 삭제 (실제 파일 삭제)
   *
   * @param filename 파일명
   */
  public void deletePhysicalFile(String filename) {
    fileService.deleteFile(filename);
    log.info("이미지 물리 파일 삭제 완료: {}", filename);
  }
}
