package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "folders")
public class Folder extends BaseEntity {

  @Column(nullable = false)
  private String name;

  /** The real path of the folder in the file system */
  @Column(nullable = false)
  private String path;

  private Long size = 0L;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "parent_folder_id")
  private Folder parentFolder;

  @OneToMany(mappedBy = "parentFolder")
  private List<Folder> subFolders = new ArrayList<>();

  @OneToMany(mappedBy = "folder")
  private List<File> files = new ArrayList<>();
}
