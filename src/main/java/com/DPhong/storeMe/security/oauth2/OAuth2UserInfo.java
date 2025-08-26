package com.DPhong.storeMe.security.oauth2;

import com.DPhong.storeMe.enums.LoginProvider;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class OAuth2UserInfo {
  protected final Map<String, Object> attributes;

  public abstract String getId();

  public abstract String getName();

  public abstract String getEmail();

  public abstract LoginProvider getProvider();
}
