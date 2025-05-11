package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.entity.RefreshToken;

public interface RefreshTokenService {

  RefreshToken findByToken(String token);

  RefreshToken generateRefreshToken(Long userId);

  RefreshToken verifyRefreshToken(String token);

  void deleteRefreshToken(RefreshToken refreshToken);
}
