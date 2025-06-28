package com.DPhong.storeMe.dto.fileSystemNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FSRequestDTO {
  // if folderId is null, it means the root folder
  private Long parentId;
}
