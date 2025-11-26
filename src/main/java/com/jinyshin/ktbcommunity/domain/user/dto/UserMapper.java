package com.jinyshin.ktbcommunity.domain.user.dto;

import com.jinyshin.ktbcommunity.domain.image.dto.response.ImageUrlsResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;

public final class UserMapper {

  private UserMapper() {
  }

  public static UserInfoResponse toUserInfo(User user, ImageUrlsResponse profileImageUrls) {
    return new UserInfoResponse(
        user.getUserId(),
        user.getEmail(),
        user.getNickname(),
        profileImageUrls
    );
  }

  public static UpdatedProfileResponse toUpdatedProfile(User user,
      ImageUrlsResponse profileImageUrls) {
    return new UpdatedProfileResponse(
        user.getUserId(),
        user.getEmail(),
        user.getNickname(),
        profileImageUrls,
        user.getUpdatedAt()
    );
  }
}
