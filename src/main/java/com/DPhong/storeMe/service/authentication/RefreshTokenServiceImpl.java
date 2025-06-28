package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.entity.RefreshToken;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.repository.RefreshTokenRepository;
import com.DPhong.storeMe.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  @Value("${jwt.refresh-token-expiration}")
  private Long refreshExpiration;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Override
  public RefreshToken findByToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
  }

  @Override
  public RefreshToken generateRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    refreshToken.setToken(java.util.UUID.randomUUID().toString());
    refreshToken.setExpiratedAt(Instant.now().plusMillis(refreshExpiration));
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public RefreshToken verifyRefreshToken(String token) {
    RefreshToken refreshToken = findByToken(token);
    if (refreshToken.getExpiratedAt().isBefore(Instant.now())) {
      deleteRefreshToken(refreshToken);
      throw new AuthException(ErrorCode.TOKEN_EXPIRED);
    }
    return refreshToken;
  }

  @Override
  public void deleteRefreshToken(RefreshToken token) {
    refreshTokenRepository.delete(token);
  }
}
