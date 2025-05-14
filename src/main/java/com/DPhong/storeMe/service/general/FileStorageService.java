package com.DPhong.storeMe.service.general;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  /** Init the storage folder */
  void init();

  boolean exists(String path);

  String createFolder(String path, String folderName);

  void deleteFolder(String path);

  void renameFolder(String path, String oldName, String newName);

  String storeFile(String path, MultipartFile file);

  Resource loadFileAsResource(String path);

  void deleteFile(String path);
}
