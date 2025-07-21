package com.DPhong.storeMe.dto.fileSystemNode;

import com.DPhong.storeMe.enums.FSAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFSNodeRequestDTO {
  private Long parentId;

  @NotBlank
  @Size(max = 255, message = "Max length of name is 255 characters")
  private String name;

  @NotNull private FSAction action;
  private boolean isHidden = false;
  private boolean isLocked = false;
}
