package com.DPhong.storeMe.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;
}
