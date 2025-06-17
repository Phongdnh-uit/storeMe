package com.DPhong.storeMe.dto.fileSystemNode;

import com.DPhong.storeMe.enums.FSAction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFSNodeRequestDTO {
  private String name;
  private Long parentId;
  private FSAction action;
}
