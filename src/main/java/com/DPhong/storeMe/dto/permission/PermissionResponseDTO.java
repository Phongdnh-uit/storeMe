package com.DPhong.storeMe.dto.permission;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.HttpMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponseDTO extends BaseEntity {
  private String name;
  private String urlPattern;
  private HttpMethod method;
  private String description;
}
