package com.jinyshin.ktbcommunity.domain.image.dto;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessedFiles {

  private final File jpgFile;
  // TODO: WebP 지원 추가 예정
}