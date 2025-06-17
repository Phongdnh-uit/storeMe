package com.DPhong.storeMe.service.general;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  /** Init the storage folder */
  void init();

  boolean exists(String path);

  String storeFile(String path, String name, MultipartFile file);

  Resource loadFileAsResource(String path);

  void deleteFile(String path);
}
