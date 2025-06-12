package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File extends FileSystemNode {

  @Column(nullable = false)
  private String mimeType;

  @Column(nullable = false)
  private String extension;

  /** The real path of the file in the file system */
  @Column(nullable = false)
  private String blobKey;

  private Instant lastAccessed;

  @ManyToOne
  @JoinColumn(name = "folder_id")
  private Folder folder;

  @Override
  public boolean isDirectory() {
    return false;
  }
}
