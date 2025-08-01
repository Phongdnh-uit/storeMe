package com.DPhong.storeMe.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TOTPResponseDTO {
  private String secret;
  private String qrCodeUrl;
}
