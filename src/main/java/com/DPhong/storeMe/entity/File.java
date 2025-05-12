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
public class File extends BaseEntity {

  @Column(nullable = false)
  private String name;

  private Long size;

  @Column(nullable = false)
  private String mimeType;

  @Column(nullable = false)
  private String extension;

  /** The real path of the file in the file system */
  @Column(nullable = false)
  private String path;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** The hash of the file, used to check if the file is already uploaded */
  private String hash;

  private Instant lastAccessed;

  @ManyToOne
  @JoinColumn(name = "folder_id")
  private Folder folder;
}
