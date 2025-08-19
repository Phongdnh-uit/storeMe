package com.DPhong.storeMe.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendVerifyEmailRequestDTO {
  @NotBlank(message = "Email không được để trống")
  private String email;
}
