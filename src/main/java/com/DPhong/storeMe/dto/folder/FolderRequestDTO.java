package com.DPhong.storeMe.dto.folder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderRequestDTO {
  @NotBlank(message = "Name is required")
  private String name;

  @Schema(
      description = "id của folder cha, nếu là tạo trong thư mục gốc thì để null",
      example = "null")
  private Long parentId;
}
