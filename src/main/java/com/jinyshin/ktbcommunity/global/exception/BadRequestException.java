package com.jinyshin.ktbcommunity.global.exception;

public class BadRequestException extends ApiException {

  public BadRequestException(ApiErrorCode errorCode) {
    super(errorCode);
  }

  public static BadRequestException sameAsCurrentPassword() {
    return new BadRequestException(ApiErrorCode.SAME_AS_CURRENT_PASSWORD);
  }

  public static BadRequestException primaryImageNotInImageIds() {
    return new BadRequestException(ApiErrorCode.PRIMARY_IMAGE_NOT_IN_IMAGE_IDS);
  }

  public static BadRequestException primaryImageNotFoundInPost() {
    return new BadRequestException(ApiErrorCode.PRIMARY_IMAGE_NOT_FOUND_IN_POST);
  }

  public static BadRequestException imageExpired() {
    return new BadRequestException(ApiErrorCode.IMAGE_EXPIRED);
  }

  public static BadRequestException imageAlreadyUsed() {
    return new BadRequestException(ApiErrorCode.IMAGE_ALREADY_USED);
  }
}
