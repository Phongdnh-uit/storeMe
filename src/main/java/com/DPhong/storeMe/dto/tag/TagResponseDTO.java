package com.DPhong.storeMe.dto.tag;

import com.DPhong.storeMe.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponseDTO extends BaseEntity {
  private String name;
  private String description;
  private Long ownerId;
}
