package com.DPhong.storeMe.dto.authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
}
