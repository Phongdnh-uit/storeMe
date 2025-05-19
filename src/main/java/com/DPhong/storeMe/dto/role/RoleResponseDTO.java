package com.DPhong.storeMe.dto.role;

import com.DPhong.storeMe.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDTO extends BaseEntity {
  private String name;
  private String description;
}
