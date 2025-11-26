package com.jinyshin.ktbcommunity.domain.image.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.IMAGE_METADATA_SAVED;

import com.jinyshin.ktbcommunity.domain.image.dto.request.ImageMetadataRequest;
import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageMetadataResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.service.ImageService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/metadata")
  public ResponseEntity<ApiResponse<ImageMetadataResponse>> saveMetadata(
      @Valid @RequestBody ImageMetadataRequest request
  ) {
    Image image = imageService.saveMetadata(request);

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
        .body(ApiResponse.success(IMAGE_METADATA_SAVED, response));
  }
}