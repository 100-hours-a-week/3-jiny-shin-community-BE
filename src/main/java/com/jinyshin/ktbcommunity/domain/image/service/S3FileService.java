package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.global.config.S3Properties;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService implements FileService {

  private final S3Client s3Client;
  private final S3Properties s3Properties;
  private final Environment environment;

  // S3에 파일을 업로드하고 저장 경로를 반환 경로 구조: {bucket}/{environment}/{image-type}/{filename}
  @Override
  public String saveFile(File file, String filename) {
    String bucketName = s3Properties.getBucket();
    String s3Key = buildS3Key(filename);

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .contentType(determineContentType(filename))
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
      log.info("S3 파일 저장 완료: {}/{}", bucketName, s3Key);

      return s3Key;
    } catch (S3Exception e) {
      log.error("S3 파일 저장 실패: {}", s3Key, e);
      throw new ApiException(ApiErrorCode.IMAGE_SAVE_FAILED);
    }
  }

  @Override
  public void deleteFile(String filename) {
    String bucketName = s3Properties.getBucket();
    String s3Key = buildS3Key(filename);

    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .build();

      s3Client.deleteObject(deleteObjectRequest);
      log.info("S3 파일 삭제 완료: {}/{}", bucketName, s3Key);
    } catch (S3Exception e) {
      log.error("S3 파일 삭제 실패: {}", s3Key, e);
      throw new ApiException(ApiErrorCode.IMAGE_DELETE_FAILED);
    }
  }

  @Override
  public boolean existsFile(String filename) {
    String bucketName = s3Properties.getBucket();
    String s3Key = buildS3Key(filename);

    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .build();

      s3Client.headObject(headObjectRequest);
      return true;
    } catch (S3Exception e) {
      if (e.statusCode() == 404) {
        return false;
      }
      log.error("S3 파일 존재 확인 실패: {}", s3Key, e);
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
  }

  @Override
  public String getPublicUrl(String filename) {
    String bucketName = s3Properties.getBucket();
    String s3Key = buildS3Key(filename);
    String region = s3Properties.getRegion();

    // AWS S3 기본 URL
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
  }

  // S3 Key 생성 구조: {environment}/{image-type}/{filename}
  private String buildS3Key(String filename) {
    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }

    String env = determineEnvironment();
    String imageType = extractImageType(filename);

    return String.format("%s/%s/%s", env, imageType, filename);
  }

  // Spring Environment로 활성 프로파일 확인
  private String determineEnvironment() {
    String[] profiles = environment.getActiveProfiles();

    for (String profile : profiles) {
      if (profile.equals("prod")) {
        return "prod";
      } else if (profile.equals("test")) {
        return "test";
      }
    }
    return "dev";
  }

  private String extractImageType(String filename) {
    return ImageType.fromFilename(filename).getFileNameSuffix();
  }

  private String determineContentType(String filename) {
    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
      return "image/jpeg";
    } else if (filename.endsWith(".png")) {
      return "image/png";
    } else if (filename.endsWith(".webp")) {
      return "image/webp";
    }
    return "application/octet-stream";
  }
}
