package com.jinyshin.ktbcommunity.domain.user.dto;

import com.jinyshin.ktbcommunity.domain.user.dto.response.UpdatedProfileResponse;
import com.jinyshin.ktbcommunity.domain.user.dto.response.UserInfoResponse;
import com.jinyshin.ktbcommunity.domain.user.entity.User;

public final class UserMapper {

  private UserMapper() {
  }

  public static UserInfoResponse toUserInfo(User user) {
    return new UserInfoResponse(
        user.getUserId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImage() != null ? user.getProfileImage().getFilename() : null
    );
  }

  public static UpdatedProfileResponse toUpdatedProfile(User user) {
    return new UpdatedProfileResponse(
        user.getUserId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImage() != null ? user.getProfileImage().getFilename() : null,
        user.getUpdatedAt()
    );
  }
}
