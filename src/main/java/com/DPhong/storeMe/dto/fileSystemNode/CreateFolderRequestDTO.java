package com.DPhong.storeMe.dto.fileSystemNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequestDTO extends FSRequestDTO {

  @NotBlank
  @Size(max = 255, message = "Max length is 255 characters")
  String name;

  private boolean isHidden = false;
  private boolean isLocked = false;
}
