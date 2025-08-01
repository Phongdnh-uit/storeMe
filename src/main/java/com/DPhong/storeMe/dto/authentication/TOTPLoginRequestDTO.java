package com.DPhong.storeMe.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TOTPLoginRequestDTO extends TOTPRequestDTO {
  @NotBlank private String pendingCode;
}
