package com.DPhong.storeMe.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
  @NotNull(message = "User ID is required")
  private Long userId;

  @NotBlank(message = "New password is required")
  private String newPassword;

  @NotBlank(message = "Code is required")
  private String code;
}
