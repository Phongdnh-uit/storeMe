package com.DPhong.storeMe.service.general;

import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.exception.StorageException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService {

  private final Path rootPath;

  public StorageServiceImpl(@Value("${storage.root.location}") String rootStorageLocation) {
    this.rootPath = Paths.get(rootStorageLocation);
  }

  // ============================ STORAGE INIT ============================
  /**
   * Initialize the storage location by creating the directory if it does not exist.
   *
   * @throws StorageException if the storage location cannot be initialized
   * @throws SecurityException if the storage location is not accessible
   * @throws IOException if an I/O error occurs
   */
  @Override
  @PostConstruct
  public void init() {
    // If the root path does not exist, create it
    if (Files.notExists(rootPath)) {
      try {
        Files.createDirectories(rootPath);
        return;
      } catch (Exception e) {
        throw new StorageException(
            ErrorCode.BLOB_SERVICE_UNAVAILABLE, "Không thể khởi tạo thư mục lưu trữ");
      }
    }

    // If the root path exists, check if it is a directory and writable
    if (!Files.isDirectory(rootPath)) {
      throw new StorageException(
          ErrorCode.BLOB_SERVICE_UNAVAILABLE, "Thư mục lưu trữ không phải là một thư mục");
    }

    if (!Files.isWritable(rootPath)) {
      throw new StorageException(
          ErrorCode.BLOB_SERVICE_UNAVAILABLE, "Thư mục lưu trữ không có quyền ghi");
    }
  }

  // ============================ CHECK EXISTS PATH ============================
  /**
   * Check if a file or folder exists at the specified path. the path is relative to the root
   * storage location.
   *
   * @param path the path to check
   * @return true if the file or folder exists, false otherwise
   */
  @Override
  public boolean exists(String path) {
    Path filePath = rootPath.resolve(path);
    return Files.exists(filePath);
  }

  // ============================ STORE FILE ============================
  /**
   * Store a file at the specified path.
   *
   * @param path the path where the file should be stored
   * @param file the file to store
   * @throws StorageException if the file is empty or cannot be stored
   */
  @Override
  public String storeFile(String path, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new StorageException(ErrorCode.FILE_WRITE_FAILED, "File bị rỗng hoặc không hợp lệ");
    }
    try {
      // Sanitize the path to ensure it is within the root storage location
      Path targetPath = sanitizePath(path);
      Files.createDirectories(targetPath.getParent());
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }
      return rootPath.relativize(targetPath).toString();
    } catch (Exception e) {
      throw new StorageException(ErrorCode.FILE_WRITE_FAILED);
    }
  }

  // ============================ COPY FILE ============================

  /**
   * Copy a file from the source path to the target path.
   *
   * @param sourcePath the path of the file to copy
   * @param targetPath the path where the file should be copied
   * @return the relative path of the copied file
   * @throws StorageException if the source file does not exist or cannot be copied
   */
  @Override
  public String copyFile(String sourcePath, String targetPath) {
    Path sourceLocation = sanitizePath(sourcePath);
    Path targetLocation = sanitizePath(targetPath);

    if (Files.notExists(sourceLocation)) {
      throw new StorageException(ErrorCode.FILE_NOT_FOUND);
    }

    try {
      // Ensure the target directory exists
      Files.createDirectories(targetLocation.getParent());
      Files.copy(sourceLocation, targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return rootPath.relativize(targetLocation).toString();
    } catch (IOException e) {
      throw new StorageException(ErrorCode.FILE_WRITE_FAILED);
    }
  }

  // ============================ LOAD FILE AS RESOURCE ============================
  /**
   * Load a file as a resource from the specified path.
   *
   * @param path the path where the file is located
   * @param filename the name of the file to load
   * @return the resource representing the file
   * @throws StorageException if the file does not exist or cannot be read
   */
  @Override
  public Resource loadFileAsResource(String path) {
    Path targetLocation = sanitizePath(path);
    if (Files.notExists(targetLocation)) {
      throw new StorageException(ErrorCode.FILE_NOT_FOUND);
    }
    try {
      Resource resource = new UrlResource(targetLocation.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException(ErrorCode.FILE_ACCESS_DENIED);
      }
    } catch (MalformedURLException e) {
      throw new StorageException(ErrorCode.FILE_PATH_INVALID);
    }
  }

  // ============================ DELETE FILE ============================
  /**
   * Delete a file at the specified path.
   *
   * @param path the path of the file to delete
   * @param filename the name of the file to delete
   * @throws StorageException if the file does not exist or cannot be deleted
   */
  @Override
  public void deleteFile(String path) {
    Path targetLocation = sanitizePath(path);
    if (Files.notExists(targetLocation)) {
      throw new StorageException(ErrorCode.FILE_NOT_FOUND);
    }
    try {
      Files.delete(targetLocation);
    } catch (Exception e) {
      throw new StorageException(ErrorCode.FILE_DELETE_FAILED);
    }
  }

  // ========================================HELPER==================================
  /** Sanitize the given path to ensure it is within the root storage location. */
  private Path sanitizePath(String path) {
    Path resolvedPath = rootPath.resolve(path).normalize();
    if (!resolvedPath.startsWith(rootPath)) {
      throw new StorageException(ErrorCode.FILE_PATH_INVALID);
    }
    return resolvedPath;
  }
}
