package com.DPhong.storeMe.dto.folder;

import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.BaseEntity;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderResponseDTO extends BaseEntity {
  private String name;

  private String path;

  private Long size = 0L;

  private UserResponseDTO user;

  private List<FolderResponseDTO> subFolders;

  private List<FileResponseDTO> files;
}
