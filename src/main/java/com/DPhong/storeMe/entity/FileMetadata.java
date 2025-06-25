package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class FileMetadata extends BaseEntity {

  @OneToOne @MapsId private FSNode file;

  @Column(nullable = false)
  private String mimeType;

  @Column(nullable = false)
  private String extension;

  /** The real path of the file in the file system */
  @Column(nullable = false)
  private String blobKey;
}
