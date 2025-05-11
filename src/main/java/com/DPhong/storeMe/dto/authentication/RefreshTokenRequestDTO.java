package com.DPhong.storeMe.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {
  @NotBlank private String refreshToken;
}
