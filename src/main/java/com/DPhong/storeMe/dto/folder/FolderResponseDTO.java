package com.DPhong.storeMe.dto.folder;

import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderResponseDTO extends FSResponseDTO {

  private Long parentFolderId;

  private List<FolderResponseDTO> subFolders;

  private List<FileResponseDTO> files;

  private boolean isLocked;

  private boolean isHidden;
}
