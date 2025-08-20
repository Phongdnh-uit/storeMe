package com.DPhong.storeMe.dto.tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequestDTO {
  private String name;

  private String description;

  private boolean isSystem = false;
}
