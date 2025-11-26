package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.global.config.S3Properties;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

  private final S3Client s3Client;
  private final S3Properties s3Properties;

  public boolean imageExists(String s3Key) {
    String bucketName = s3Properties.getBucket();
    String validatedKey = validateS3Key(s3Key);

    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucketName)
          .key(validatedKey)
          .build();

      s3Client.headObject(headObjectRequest);
      return true;
    } catch (S3Exception e) {
      if (e.statusCode() == 404) {
        return false;
      }
      log.error("S3 이미지 존재 확인 실패: {}", validatedKey, e);
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
  }

  public void deleteAllFormats(String s3Path, String storedFilename, String originalExtension) {
    String baseKey = s3Path + "/" + storedFilename;

    // 원본 삭제
    deleteImage(baseKey + "_original." + originalExtension);

    // 최적화 파일 삭제 (없으면 무시)
    deleteImageIfExists(baseKey + ".jpg");
    deleteImageIfExists(baseKey + ".webp");

    // 썸네일 삭제 (없으면 무시)
    deleteImageIfExists(baseKey + "_thumb.jpg");
    deleteImageIfExists(baseKey + "_thumb.webp");

    log.info("모든 포맷 이미지 삭제 완료: {}", baseKey);
  }

  private void deleteImage(String s3Key) {
    String bucketName = s3Properties.getBucket();
    String validatedKey = validateS3Key(s3Key);

    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(validatedKey)
          .build();

      s3Client.deleteObject(deleteObjectRequest);
      log.info("S3 이미지 삭제 완료: {}/{}", bucketName, validatedKey);
    } catch (S3Exception e) {
      log.error("S3 이미지 삭제 실패: {}", validatedKey, e);
      throw new ApiException(ApiErrorCode.IMAGE_DELETE_FAILED);
    }
  }

  private void deleteImageIfExists(String s3Key) {
    try {
      deleteImage(s3Key);
    } catch (ApiException e) {
      log.debug("이미지 파일 없음: {}", s3Key);
    }
  }

  private String validateS3Key(String s3Key) {
    if (s3Key.contains("..")) {
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
    return s3Key;
  }
}