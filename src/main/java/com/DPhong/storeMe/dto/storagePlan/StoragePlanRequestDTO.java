package com.DPhong.storeMe.dto.storagePlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoragePlanRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "Storage limit is required")
  private Long storageLimit;
}
