package com.jinyshin.ktbcommunity.domain.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("LocalFileService 단위 테스트")
class LocalFileServiceTest {

  private LocalFileService localFileService;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    localFileService = new LocalFileService();
    // @Value로 주입되는 uploadPath를 테스트용 임시 디렉토리로 설정
    ReflectionTestUtils.setField(localFileService, "uploadPath", tempDir.toString());
    localFileService.init();
  }

  @Test
  @DisplayName("정상적인 파일을 저장하면 업로드 디렉토리에 파일이 생성된다")
  void saveFile_ValidFile_SavesSuccessfully() throws IOException {
    // Given
    File tempFile = Files.createTempFile(tempDir, "temp_", ".jpg").toFile();
    Files.writeString(tempFile.toPath(), "test content");
    String filename = "test-file.jpg";

    // When
    String savedPath = localFileService.saveFile(tempFile, filename);

    // Then
    assertThat(savedPath).contains(filename);
    assertThat(localFileService.existsFile(filename)).isTrue();

    // Cleanup
    localFileService.deleteFile(filename);
  }

  @Test
  @DisplayName("Path Traversal 공격이 포함된 파일명으로 저장하면 예외가 발생한다")
  void saveFile_PathTraversalFilename_ThrowsException() throws IOException {
    // Given
    File tempFile = Files.createTempFile(tempDir, "temp_", ".jpg").toFile();
    String maliciousFilename = "../../../etc/passwd";

    // When & Then
    assertThatThrownBy(() -> localFileService.saveFile(tempFile, maliciousFilename))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);

    // Cleanup
    tempFile.delete();
  }

  @Test
  @DisplayName("null 파일명으로 저장하면 예외가 발생한다")
  void saveFile_NullFilename_ThrowsException() throws IOException {
    // Given
    File tempFile = Files.createTempFile(tempDir, "temp_", ".jpg").toFile();

    // When & Then
    assertThatThrownBy(() -> localFileService.saveFile(tempFile, null))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);

    // Cleanup
    tempFile.delete();
  }

  @Test
  @DisplayName("빈 파일명으로 저장하면 예외가 발생한다")
  void saveFile_EmptyFilename_ThrowsException() throws IOException {
    // Given
    File tempFile = Files.createTempFile(tempDir, "temp_", ".jpg").toFile();

    // When & Then
    assertThatThrownBy(() -> localFileService.saveFile(tempFile, ""))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);

    // Cleanup
    tempFile.delete();
  }

  @Test
  @DisplayName("저장된 파일을 로드하면 File 객체가 반환된다")
  void loadFile_ExistingFile_ReturnsFile() throws IOException {
    // Given
    String filename = "test-load.jpg";
    Path filePath = tempDir.resolve(filename);
    Files.writeString(filePath, "test content");

    // When
    File loadedFile = localFileService.loadFile(filename);

    // Then
    assertThat(loadedFile).exists();
    assertThat(loadedFile.getName()).isEqualTo(filename);

    // Cleanup
    localFileService.deleteFile(filename);
  }

  @Test
  @DisplayName("존재하지 않는 파일을 로드하면 예외가 발생한다")
  void loadFile_NonExistingFile_ThrowsException() {
    // Given
    String nonExistingFilename = "non-existing.jpg";

    // When & Then
    assertThatThrownBy(() -> localFileService.loadFile(nonExistingFilename))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);
  }

  @Test
  @DisplayName("저장된 파일을 삭제하면 파일이 제거된다")
  void deleteFile_ExistingFile_DeletesSuccessfully() throws IOException {
    // Given
    String filename = "test-delete.jpg";
    Path filePath = tempDir.resolve(filename);
    Files.writeString(filePath, "test content");
    assertThat(Files.exists(filePath)).isTrue();

    // When
    localFileService.deleteFile(filename);

    // Then
    assertThat(Files.exists(filePath)).isFalse();
    assertThat(localFileService.existsFile(filename)).isFalse();
  }

  @Test
  @DisplayName("존재하지 않는 파일을 삭제해도 예외가 발생하지 않는다")
  void deleteFile_NonExistingFile_DoesNotThrowException() {
    // Given
    String nonExistingFilename = "non-existing.jpg";

    // When & Then - 예외가 발생하지 않아야 함
    localFileService.deleteFile(nonExistingFilename);
  }

  @Test
  @DisplayName("저장된 파일이 존재하면 true를 반환한다")
  void existsFile_ExistingFile_ReturnsTrue() throws IOException {
    // Given
    String filename = "test-exists.jpg";
    Path filePath = tempDir.resolve(filename);
    Files.writeString(filePath, "test content");

    // When
    boolean exists = localFileService.existsFile(filename);

    // Then
    assertThat(exists).isTrue();

    // Cleanup
    localFileService.deleteFile(filename);
  }

  @Test
  @DisplayName("존재하지 않는 파일이면 false를 반환한다")
  void existsFile_NonExistingFile_ReturnsFalse() {
    // Given
    String nonExistingFilename = "non-existing.jpg";

    // When
    boolean exists = localFileService.existsFile(nonExistingFilename);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("슬래시가 포함된 파일명으로 존재 확인하면 예외가 발생한다")
  void existsFile_FilenameWithSlash_ThrowsException() {
    // Given
    String maliciousFilename = "subdir/file.jpg";

    // When & Then
    assertThatThrownBy(() -> localFileService.existsFile(maliciousFilename))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);
  }

  @Test
  @DisplayName("백슬래시가 포함된 파일명으로 존재 확인하면 예외가 발생한다")
  void existsFile_FilenameWithBackslash_ThrowsException() {
    // Given
    String maliciousFilename = "subdir\\file.jpg";

    // When & Then
    assertThatThrownBy(() -> localFileService.existsFile(maliciousFilename))
        .isInstanceOf(ApiException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiErrorCode.IMAGE_PROCESSING_FAILED);
  }
}