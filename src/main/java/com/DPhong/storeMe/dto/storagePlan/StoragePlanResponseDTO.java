package com.DPhong.storeMe.dto.storagePlan;

import com.DPhong.storeMe.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoragePlanResponseDTO extends BaseEntity {
  private String name;
  private Double price;
  private Long timeOfPlan;
  private Long storageLimit;
  private String description;
}
