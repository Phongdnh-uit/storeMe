package com.DPhong.storeMe.dto.file;

import java.time.Instant;

import com.DPhong.storeMe.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponseDTO extends BaseEntity {
  private String name;

  private Long size;

  private String mimeType;

  private String extension;

  private String path;

  private Long userId;

  private Instant lastAccessed;

  private Long folderId;
}
