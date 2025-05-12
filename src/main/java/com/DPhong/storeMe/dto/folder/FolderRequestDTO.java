package com.DPhong.storeMe.dto.folder;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;

  private Long parentId;
}
