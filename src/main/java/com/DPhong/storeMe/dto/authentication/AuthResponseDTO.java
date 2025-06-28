package com.DPhong.storeMe.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
}
