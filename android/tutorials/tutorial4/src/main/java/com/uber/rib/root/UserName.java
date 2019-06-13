package com.uber.rib.root;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UserName {

  @NonNull
  public static UserName create(String userName) {
    return new AutoValue_UserName(userName);
  }

  public abstract String getUserName();
}
