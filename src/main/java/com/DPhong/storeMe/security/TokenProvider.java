package com.DPhong.storeMe.security;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenProvider {

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Value("${jwt.access-token-expiration}")
  private Long expirationTime;

  public String generateAccessToken(Long userId) {
    Instant now = Instant.now();
    Instant expiration = now.plusMillis(expirationTime);

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(expiration)
            .build();
    JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }

  public Jwt validateAccessToken(String token) {
    try {
      return this.jwtDecoder.decode(token);
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT token", e);
    }
  }
}
