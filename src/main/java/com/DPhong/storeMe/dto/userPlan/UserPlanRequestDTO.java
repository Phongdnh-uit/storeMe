package com.DPhong.storeMe.dto.userPlan;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPlanRequestDTO {
  @NotNull(message = "user id is required")
  private Long userId;

  @NotNull(message = "storage plan id is required")
  private Long storagePlanId;
}
