package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authentication.RefreshToken;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends SimpleRepository<RefreshToken, String> {
  Optional<RefreshToken> findByToken(String token);
}
