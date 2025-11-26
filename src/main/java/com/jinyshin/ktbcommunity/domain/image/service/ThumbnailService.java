package com.jinyshin.ktbcommunity.domain.image.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

/**
 * 썸네일 이미지 비동기 생성 서비스 (LambdaAsyncClient 사용)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailService {

  private final S3Service s3Service;
  private final LambdaAsyncClient lambdaAsyncClient;
  private final ObjectMapper objectMapper;

  @Value("${lambda.thumbnail.function-name:thumbnail-generator}")
  private String thumbnailFunctionName;

  /**
   * 게시물 대표 이미지 선택 시 썸네일 비동기 생성
   *
   * @param image 대표 이미지 엔티티
   */
  public void generateThumbnail(Image image) {
    String s3Path = image.getS3Path();
    String storedFilename = image.getStoredFilename();
    String baseKey = s3Path + "/" + storedFilename;
    String thumbKey = baseKey + "_thumb.jpg";

    // 이미 썸네일이 존재하면 생성하지 않음 (중복 방지)
    if (s3Service.imageExists(thumbKey)) {
      log.info("썸네일 이미지 이미 존재: {}", thumbKey);
      return;
    }

    try {
      // Lambda 페이로드 생성
      Map<String, String> payload = new HashMap<>();
      payload.put("storedFilename", storedFilename);
      payload.put("originalExtension", image.getOriginalExtension());
      payload.put("s3Path", s3Path);

      String payloadJson = objectMapper.writeValueAsString(payload);

      // Lambda 비동기 호출 (Non-blocking)
      InvokeRequest invokeRequest = InvokeRequest.builder()
          .functionName(thumbnailFunctionName)
          .payload(SdkBytes.fromUtf8String(payloadJson))
          .build();

      lambdaAsyncClient.invoke(invokeRequest)
          .whenComplete((response, throwable) -> {
            if (throwable != null) {
              log.error("썸네일 이미지 생성 실패: storedFilename={}, s3Path={}, error={}",
                  storedFilename, s3Path, throwable.getMessage());
            } else {
              log.info("썸네일 이미지 생성 요청 완료: storedFilename={}, s3Path={}, statusCode={}",
                  storedFilename, s3Path, response.statusCode());
            }
          });

      log.info("썸네일 이미지 생성 요청 전송: storedFilename={}, s3Path={}",
          storedFilename, s3Path);

    } catch (Exception e) {
      // 썸네일 생성 실패해도 예외 던지지 않음
      // 썸네일 없으면 고품질 이미지로 fallback
      log.error("썸네일 이미지 생성 요청 실패: storedFilename={}, s3Path={}, error={}",
          storedFilename, s3Path, e.getMessage());
    }
  }
}