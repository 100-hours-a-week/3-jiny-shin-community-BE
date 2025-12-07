package com.jinyshin.ktbcommunity.domain.image.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.IMAGE_METADATA_SAVED;

import com.jinyshin.ktbcommunity.domain.image.dto.request.ImageMetadataRequest;
import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageMetadataResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.service.ImageService;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image", description = "이미지 관련 API")
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/metadata")
  @Operation(
      summary = "이미지 메타데이터 저장",
      description = "Lambda에서 업로드한 이미지의 메타데이터를 DB에 저장합니다."
  )
  @ApiResponse(
      responseCode = "201",
      description = "메타데이터 저장 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  @ApiResponse(
      responseCode = "400",
      description = "잘못된 입력값",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<ImageMetadataResponse>> saveMetadata(
      @Parameter(description = "이미지 메타데이터 요청 데이터")
      @Valid @RequestBody ImageMetadataRequest request,
      @Parameter(hidden = true) @RequestAttribute Long userId
  ) {
    Image image = imageService.saveMetadata(request, userId);

    ImageMetadataResponse response = new ImageMetadataResponse(
        image.getImageId(),
        image.getStoredFilename(),
        image.getOriginalExtension(),
        image.getS3Path(),
        image.getImageType(),
        image.getCreatedAt()
    );

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(IMAGE_METADATA_SAVED, response));
  }
}
