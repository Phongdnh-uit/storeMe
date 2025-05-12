package com.DPhong.storeMe.service.general;

public interface FileStorageService {

  /** Init the storage folder */
  void init();

  boolean exists(String path);

  String createFolder(String path, String folderName);

  void deleteFolder(String path);

  // void store(MultipartFile file);

  // Stream<Path> loadAll();

  // Path load(String filename);

  // Resource loadAsResource(String filename);

  // void deleteAll();
}
