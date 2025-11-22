package com.jinyshin.ktbcommunity.domain.image.service;

import java.io.File;

public interface FileService {

  /**
   * 파일을 저장하고 저장 경로를 반환
   *
   * @param file     저장할 파일
   * @param filename 저장할 파일명 (확장자 포함)
   * @return 저장된 파일의 전체 경로
   */
  String saveFile(File file, String filename);

  /**
   * 파일을 로드
   *
   * @param filename 로드할 파일명
   * @return File 객체
   */
  File loadFile(String filename);

  /**
   * 파일을 삭제
   *
   * @param filename 삭제할 파일명
   */
  void deleteFile(String filename);

  /**
   * 파일 존재 여부 확인
   *
   * @param filename 확인할 파일명
   * @return 존재 여부
   */
  boolean existsFile(String filename);

}
