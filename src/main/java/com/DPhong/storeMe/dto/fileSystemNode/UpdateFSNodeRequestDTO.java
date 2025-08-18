package com.DPhong.storeMe.dto.fileSystemNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFSNodeRequestDTO {
  @NotBlank
  @Size(max = 255, message = "Max length of name is 255 characters")
  private String name;

  private boolean isHidden = false;
  private boolean isLocked = false;
}
