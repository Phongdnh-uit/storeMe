package com.DPhong.storeMe.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
  @NotBlank
  @Pattern(
      regexp = "^[a-zA-Z0-9]{3,20}$",
      message = "Username must be 3-20 characters long and can only contain letters and numbers")
  private String username;

  @NotBlank @Email private String email;
}
