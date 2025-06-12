package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.FolderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Folder extends FileSystemNode {

  @ManyToOne
  @JoinColumn(name = "parent_folder_id")
  private Folder parentFolder;

  @OneToMany(mappedBy = "parentFolder")
  private List<Folder> subFolders = new ArrayList<>();

  @OneToMany(mappedBy = "folder")
  private List<File> files = new ArrayList<>();

  @Column(columnDefinition = "boolean default false")
  private boolean isLocked = false;

  @Column(columnDefinition = "boolean default false")
  private boolean isHidden = false;

  @Enumerated(EnumType.STRING)
  private FolderType type = FolderType.NORMAL;

  @Override
  public boolean isDirectory() {
    return true;
  }
}
