package com.DPhong.storeMe.dto.storagePlan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoragePlanRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Price is required")
  private Double price;

  @NotNull(message = "Time of plan is required")
  private Long timeOfPlan;

  @NotNull(message = "Storage limit is required")
  @Min(value = 0, message = "Storage limit must be greater than or equal to 0")
  private Long storageLimit;

  private String description;
}
