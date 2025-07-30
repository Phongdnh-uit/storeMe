package com.DPhong.storeMe.service.authentication;

public interface BlacklistTokenService {

  void addToBlacklist(String token);

  boolean isBlacklisted(String token);

  void removeFromBlacklist(String token);
}
