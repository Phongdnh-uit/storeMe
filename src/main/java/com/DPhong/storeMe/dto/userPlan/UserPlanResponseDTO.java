package com.DPhong.storeMe.dto.userPlan;

import com.DPhong.storeMe.dto.storagePlan.StoragePlanResponseDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.BaseEntity;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPlanResponseDTO extends BaseEntity {
  private UserResponseDTO user;

  private String isActive;

  private StoragePlanResponseDTO storagePlan;

  private Instant assignedAt;

  private Instant expiredAt;
}
