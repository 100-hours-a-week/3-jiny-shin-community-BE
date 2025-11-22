package com.jinyshin.ktbcommunity.domain.image.service;

import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocalFileService implements FileService {

  @Value("${file.upload.path}")
  private String uploadPath;

  @PostConstruct
  public void init() {
    try {
      Path uploadDir = Paths.get(uploadPath);
      if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
        log.info("파일 저장 디렉토리 생성: {}", uploadPath);
      }
    } catch (IOException e) {
      log.error("파일 저장 디렉토리 생성 실패: {}", uploadPath, e);
      throw new ApiException(ApiErrorCode.IMAGE_SAVE_FAILED);
    }
  }

  @Override
  public String saveFile(File file, String filename) {
    validateFilename(filename);

    try {
      Path sourcePath = file.toPath();
      Path targetPath = Paths.get(uploadPath, filename);

      // 파일을 영구 저장소로 이동
      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

      // 임시 파일 삭제
      Files.deleteIfExists(sourcePath);

      log.info("파일 저장 완료: {}", targetPath);
      return targetPath.toString();
    } catch (IOException e) {
      log.error("파일 저장 실패: {}", filename, e);
      throw new ApiException(ApiErrorCode.IMAGE_SAVE_FAILED);
    }
  }

  @Override
  public File loadFile(String filename) {
    validateFilename(filename);

    Path filePath = Paths.get(uploadPath, filename);
    File file = filePath.toFile();

    if (!file.exists()) {
      log.error("파일을 찾을 수 없음: {}", filename);
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }

    return file;
  }

  @Override
  public void deleteFile(String filename) {
    validateFilename(filename);

    try {
      Path filePath = Paths.get(uploadPath, filename);
      boolean deleted = Files.deleteIfExists(filePath);

      if (deleted) {
        log.info("파일 삭제 완료: {}", filename);
      } else {
        log.warn("삭제할 파일이 존재하지 않음: {}", filename);
      }
    } catch (IOException e) {
      log.error("파일 삭제 실패: {}", filename, e);
      throw new ApiException(ApiErrorCode.IMAGE_DELETE_FAILED);
    }
  }

  @Override
  public boolean existsFile(String filename) {
    validateFilename(filename);

    Path filePath = Paths.get(uploadPath, filename);
    return Files.exists(filePath);
  }
  
  private void validateFilename(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }

    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
  }
}
