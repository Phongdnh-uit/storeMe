package com.DPhong.storeMe.service.general;

import com.DPhong.storeMe.exception.StorageException;
import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class FileStorageServiceImpl implements FileStorageService {

  private final Path rootPath;

  public FileStorageServiceImpl(@Value("${storage.root.location}") String rootStorageLocation) {
    this.rootPath = Paths.get(rootStorageLocation);
  }

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

  /**
   * Initialize the storage location by creating the directory if it does not exist.
   *
   * @throws StorageException if the storage location cannot be initialized
   */
  @Override
  @PostConstruct
  public void init() {
    if (Files.notExists(rootPath)) {
      try {
        Files.createDirectories(rootPath);
      } catch (Exception e) {
        throw new StorageException("Could not initialize storage location", e);
      }
    }
  }

  /**
   * Create a folder at the specified path with the given folder name.
   *
   * @param path the path where the folder should be created
   * @param folderName the name of the folder to create
   */
  @Override
  public String createFolder(String path, String folderName) {
    Path folderPath = rootPath.resolve(path);
    if (Files.notExists(folderPath)) {
      throw new StorageException("Path does not exist: " + path);
    }
    Path newFolderPath = folderPath.resolve(folderName);
    if (Files.exists(newFolderPath)) {
      throw new StorageException("Folder already exists: " + folderName);
    }
    try {
      Files.createDirectory(newFolderPath);
    } catch (Exception e) {
      throw new StorageException("Could not create folder: " + folderName, e);
    }
    return newFolderPath.toString();
  }

  /**
   * Delete a folder at the specified path.
   *
   * @param path the path of the folder to delete
   * @throws StorageException if the folder does not exist or cannot be deleted
   */
  @Override
  public void deleteFolder(String path) {
    Path folderPath = rootPath.resolve(path);
    if (Files.notExists(folderPath)) {
      throw new StorageException("Path does not exist: " + path);
    }
    boolean deleted = FileSystemUtils.deleteRecursively(folderPath.toFile());
    if (!deleted) {
      throw new StorageException("Could not delete folder: " + path);
    }
  }
}
