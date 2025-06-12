package com.DPhong.storeMe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FolderType {
  SHARED("Shared folder with other users, limited to one instance per user"),
  USERROOT("Personal folder for user-specific files, limited to one instance per user"),
  NORMAL("Normal folder for general use, can have multiple instances"),
  TRASH("Trash folder for deleted files, limited to one instance per user");

  private String description;
}
