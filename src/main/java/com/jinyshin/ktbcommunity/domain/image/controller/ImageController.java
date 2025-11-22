package com.jinyshin.ktbcommunity.domain.image.controller;

import com.jinyshin.ktbcommunity.domain.image.dto.ImageResponse;
import com.jinyshin.ktbcommunity.domain.image.dto.ImageUploadResponse;
import com.jinyshin.ktbcommunity.domain.image.entity.Image;
import com.jinyshin.ktbcommunity.domain.image.entity.ImageType;
import com.jinyshin.ktbcommunity.domain.image.service.ImageService;
import com.jinyshin.ktbcommunity.global.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

  private final ImageService imageService;
  
  @PostMapping
  public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam("imageType") ImageType imageType) {

    Image savedImage = imageService.uploadImage(file, imageType);
    ImageUploadResponse response = ImageUploadResponse.from(savedImage);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("이미지 업로드 성공", response));
  }
  
  @GetMapping("/{imageId}")
  public ResponseEntity<ApiResponse<ImageResponse>> getImage(@PathVariable Long imageId) {
    Image image = imageService.getImage(imageId);
    ImageResponse response = ImageResponse.from(image);

    return ResponseEntity.ok(ApiResponse.success("이미지 조회 성공", response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ImageResponse>>> getImages(
      @RequestParam("imageIds") List<Long> imageIds) {

    List<Image> images = imageService.getImages(imageIds);
    List<ImageResponse> responses = images.stream()
        .map(ImageResponse::from)
        .toList();

    return ResponseEntity.ok(ApiResponse.success("이미지 목록 조회 성공", responses));
  }

  @DeleteMapping("/{imageId}")
  public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
    imageService.deleteImage(imageId);

    return ResponseEntity.ok(ApiResponse.success("이미지 삭제 성공", null));
  }
}