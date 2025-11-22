package com.jinyshin.ktbcommunity.domain.image.processor;

import com.jinyshin.ktbcommunity.domain.image.dto.ProcessedFiles;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ImageProcessor {

  private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
  private static final Set<String> ALLOWED_FORMATS = Set.of("jpg", "jpeg", "png", "webp");

  public ProcessedFiles processImage(MultipartFile file, ImageType imageType) {
    validateFile(file);

    try {
      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
      }

      File jpgFile = compressToJPG(image, imageType);

      return new ProcessedFiles(jpgFile);
    } catch (IOException e) {
      log.error("이미지 처리 실패: {}", file.getOriginalFilename(), e);
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
  }

  private void validateFile(MultipartFile file) {
    // 파일 존재 여부
    if (file == null || file.isEmpty()) {
      throw new ApiException(ApiErrorCode.IMAGE_FILE_EMPTY);
    }

    // 파일 크기 검증
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new ApiException(ApiErrorCode.IMAGE_FILE_TOO_LARGE);
    }

    // 파일 포맷 검증
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new ApiException(ApiErrorCode.IMAGE_INVALID_FORMAT);
    }

    String extension = getFileExtension(originalFilename).toLowerCase(Locale.ROOT);
    if (!ALLOWED_FORMATS.contains(extension)) {
      throw new ApiException(ApiErrorCode.IMAGE_INVALID_FORMAT);
    }
  }

  private String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1);
  }

  private String generateFileName(ImageType imageType, String extension) {
    String uuid = UUID.randomUUID().toString();
    String postfix = imageType.getFileNamePostfix();
    return String.format("%s_%s.%s", uuid, postfix, extension);
  }

  private File compressToJPG(BufferedImage image, ImageType imageType)
      throws IOException {
    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
    if (!writers.hasNext()) {
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
    ImageWriter writer = writers.next();

    float quality = imageType.getCompressionQuality();
    String fileName = generateFileName(imageType, "jpg");
    File outputFile = File.createTempFile("temp_", "_" + fileName);

    try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile)) {
      writer.setOutput(outputStream);

      ImageWriteParam params = writer.getDefaultWriteParam();
      params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      params.setCompressionQuality(quality);

      writer.write(null, new IIOImage(image, null, null), params);
    } finally {
      writer.dispose();
    }

    return outputFile;
  }

  // TODO: WebP 변환 기능 추가
  // private File convertToWebP(BufferedImage image, ImageType imageType)
}