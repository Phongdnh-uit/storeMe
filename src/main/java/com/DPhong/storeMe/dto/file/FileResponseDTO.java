package com.DPhong.storeMe.dto.file;

import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponseDTO extends FSResponseDTO {

  private String mimeType;

  private String extension;

  private Instant lastAccessed;

  private Long folderId;
}
