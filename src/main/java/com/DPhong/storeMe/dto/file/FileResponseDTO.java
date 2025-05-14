package com.DPhong.storeMe.dto.file;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponseDTO {
  private String name;

  private Long size;

  private String mimeType;

  private String extension;

  private String path;

  private Long userId;

  private Instant lastAccessed;

  private Long folderId;
}
