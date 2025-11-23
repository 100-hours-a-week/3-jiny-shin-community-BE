package com.jinyshin.ktbcommunity.domain.image.processor;

import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.global.exception.ApiErrorCode;
import com.jinyshin.ktbcommunity.global.exception.ApiException;
import java.awt.Color;
import java.awt.Graphics2D;
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
  private static final Set<String> ALLOWED_FORMATS = Set.of("jpg", "jpeg", "png");

  // 이미지를 처리하여 JPG 파일로 반환, PNG의 경우 RGB 변환 후 JPG로 압축
  public File processImage(MultipartFile file, ImageType imageType) {
    validateFile(file);

    try {
      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new ApiException(ApiErrorCode.IMAGE_INVALID_FORMAT);
      }

      // RGB 변환 (CMYK, Grayscale, Alpha 채널 처리)
      BufferedImage rgbImage = convertToRGB(image);

      // JPG 압축
      return compressToJPG(rgbImage, imageType);
    } catch (IOException e) {
      log.error("이미지 처리 실패: {}", file.getOriginalFilename(), e);
      throw new ApiException(ApiErrorCode.IMAGE_PROCESSING_FAILED);
    }
  }

  // BufferedImage를 RGB 색상 공간으로 변환
  private BufferedImage convertToRGB(BufferedImage source) {
    // 이미 RGB 이미지라면 그대로 반환
    if (source.getType() == BufferedImage.TYPE_INT_RGB) {
      return source;
    }

    log.info("이미지 RGB 변환 시작: type={}", source.getType());

    // 새로운 RGB 이미지 생성
    BufferedImage rgbImage = new BufferedImage(
        source.getWidth(),
        source.getHeight(),
        BufferedImage.TYPE_INT_RGB);

    // Graphics2D를 사용하여 이미지 복사
    Graphics2D g = rgbImage.createGraphics();
    try {
      // 흰색 배경 설정 (투명도 처리)
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());

      // 원본 이미지를 RGB 이미지 위에 그리기
      g.drawImage(source, 0, 0, null);

      log.info("RGB 변환 완료");
      return rgbImage;
    } finally {
      g.dispose();
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
    String suffix = imageType.getFileNameSuffix();
    return String.format("%s_%s.%s", uuid, suffix, extension);
  }

  /**
   * BufferedImage를 JPG로 압축
   */
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

      // RGB 변환된 이미지를 JPG로 압축 (Bogus input colorspace 에러 방지)
      writer.write(null, new IIOImage(image, null, null), params);

      log.info("JPG 압축 완료: quality={}, size={}KB", quality, outputFile.length() / 1024);
    } finally {
      writer.dispose();
    }

    return outputFile;
  }
}