package com.contractorcontrol.api.security;

import com.contractorcontrol.api.entity.UserEntity;

public class CurrentUser {

  private final UserEntity user;

  public CurrentUser(UserEntity user) {
    this.user = user;
  }

  public UserEntity getUser() {
    return user;
  }
}
