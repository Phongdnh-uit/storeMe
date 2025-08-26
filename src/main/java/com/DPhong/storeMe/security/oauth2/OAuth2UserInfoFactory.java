package com.DPhong.storeMe.security.oauth2;

import com.DPhong.storeMe.enums.LoginProvider;
import java.util.Map;

public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
    LoginProvider loginProvider = LoginProvider.valueOf(provider.toUpperCase());
    switch (loginProvider) {
      case GOOGLE:
        return new GoogleOAuth2UserInfo(attributes);
      default:
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }
  }
}
