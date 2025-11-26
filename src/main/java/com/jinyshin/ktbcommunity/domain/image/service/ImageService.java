package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.domain.image.dto.request.ImageMetadataRequest;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.repository.ImageRepository;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ImageService {

  private final ImageRepository imageRepository;
  private final S3Service s3Service;

  // Lambda 응답 받고 메타정보 저장
  @Transactional
  public Image saveMetadata(ImageMetadataRequest request) {
    // S3 파일 존재 여부 검증 (보안)
    String s3Key = request.s3Path() + "/" + request.storedFilename() + ".jpg";
    if (!s3Service.imageExists(s3Key)) {
      log.warn("S3 파일 미존재 - s3Key: {}", s3Key);
      throw new ApiException(ApiErrorCode.FILE_NOT_FOUND_IN_S3);
    }

    // DB 저장
    Image image = new Image(
        request.storedFilename(),
        request.originalExtension(),
        request.s3Path(),
        request.imageType()
    );

    Image savedImage = imageRepository.save(image);
    log.info("이미지 메타정보 저장 완료 - imageId: {}, storedFilename: {}, s3Path: {}",
        savedImage.getImageId(), savedImage.getStoredFilename(), savedImage.getS3Path());

    return savedImage;
  }

  /**
   * 이미지 삭제 (Soft Delete) - DB 메타데이터만 Soft Delete - S3 파일은 보관 (복구 가능) - Lifecycle Policy로 일정 기간 후 자동
   * 삭제
   */
  @Transactional
  public void deleteImage(Long imageId) {
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.IMAGE_NOT_FOUND));

    // Soft Delete (S3 파일은 그대로 둠)
    imageRepository.delete(image);

    log.info("이미지 Soft Delete 완료: imageId={}", imageId);
  }

  /**
   * 이미지 완전 삭제 (Hard Delete) - DB 메타데이터 Hard Delete - S3 물리 파일 삭제 - 복구 불가능 (관리자/배치 작업 전용)
   */
  @Transactional
  public void deleteImagePermanently(Long imageId) {
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.IMAGE_NOT_FOUND));

    // S3 물리 파일 삭제
    s3Service.deleteAllFormats(
        image.getS3Path(),
        image.getStoredFilename(),
        image.getOriginalExtension()
    );

    // DB 메타데이터 Hard Delete
    imageRepository.delete(image);

    log.info("이미지 완전 삭제 완료: imageId={}, s3Path={}, storedFilename={}",
        imageId, image.getS3Path(), image.getStoredFilename());
  }
}