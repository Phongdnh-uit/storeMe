package com.DPhong.storeMe.dto.permission;

import com.DPhong.storeMe.enums.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {
  @NotBlank private String name;

  @Pattern(regexp = "^/[A-Za-z0-9/\\-_]*[A-Za-z0-9]$")
  @NotBlank
  private String urlPattern;

  @NotNull private HttpMethod method;
  private String description;
}
