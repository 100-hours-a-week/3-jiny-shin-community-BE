package com.jinyshin.ktbcommunity.domain.aigeneration.controller;

import static com.jinyshin.ktbcommunity.global.constants.ApiMessages.AI_GENERATION_REMAINING;

import com.jinyshin.ktbcommunity.domain.aigeneration.dto.response.AiGenerationRemainingResponse;
import com.jinyshin.ktbcommunity.domain.aigeneration.service.AiGenerationService;
import com.jinyshin.ktbcommunity.domain.aigeneration.service.AiGenerationService.RemainingUsage;
import com.jinyshin.ktbcommunity.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "AI Generation", description = "AI 이미지 생성 관련 API")
public class AiGenerationController {

  private final AiGenerationService aiGenerationService;

  @GetMapping("/ai-generations/remaining")
  @Operation(
      summary = "AI 생성 잔여 횟수 조회",
      description = "오늘 남은 AI 이미지 생성 횟수를 조회합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "잔여 횟수 조회 성공",
      content = @Content(schema = @Schema(implementation = BaseResponse.class))
  )
  public ResponseEntity<BaseResponse<AiGenerationRemainingResponse>> getRemaining(
      @Parameter(hidden = true) @RequestAttribute Long userId
  ) {
    RemainingUsage usage = aiGenerationService.getRemaining(userId);
    AiGenerationRemainingResponse response = new AiGenerationRemainingResponse(
        usage.remaining(), usage.limit(), usage.used());
    return ResponseEntity.ok(BaseResponse.success(AI_GENERATION_REMAINING, response));
  }
}
