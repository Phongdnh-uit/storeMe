package com.DPhong.storeMe.service.general;

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
      } catch (IOException e) {
        throw new StorageException("Could not initialize storage location", e);
      } catch (SecurityException e) {
        throw new StorageException("Storage location is not accessible", e);
      }
    }

    // If the root path exists, check if it is a directory and writable
    if (!Files.isDirectory(rootPath)) {
      throw new StorageException("Storage location is not a directory");
    }

    if (!Files.isWritable(rootPath)) {
      throw new StorageException("Storage location is not writable");
    }
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
   * Store a file at the specified path.
   *
   * @param path the path where the file should be stored
   * @param file the file to store
   * @throws StorageException if the file is empty or cannot be stored
   */
  @Override
  public String storeFile(String path, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new StorageException("Failed to store empty file.");
    }
    try {
      // Sanitize the path to ensure it is within the root storage location
      Path folderPath = sanitizePath(path);
      if (Files.notExists(folderPath)) {
        throw new StorageException("Path does not exist: " + path);
      }
      Path targetLocation = folderPath.resolve(file.getOriginalFilename());
      if (Files.exists(targetLocation)) {
        throw new StorageException("File already exists: " + file.getOriginalFilename());
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
      }
      return rootPath.relativize(targetLocation).toString();
    } catch (Exception e) {
      throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
    }
  }

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
      throw new StorageException("File does not exist");
    }
    try {
      Resource resource = new UrlResource(targetLocation.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException("Could not read file");
      }
    } catch (MalformedURLException e) {
      throw new StorageException("Failed to load file", e);
    }
  }

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
      throw new StorageException("File does not exist");
    }
    try {
      Files.delete(targetLocation);
    } catch (Exception e) {
      throw new StorageException("Failed to delete file");
    }
  }

  // ========================================HELPER==================================
  /** Sanitize the given path to ensure it is within the root storage location. */
  private Path sanitizePath(String path) {
    Path resolvedPath = rootPath.resolve(path).normalize();
    if (!resolvedPath.startsWith(rootPath)) {
      throw new StorageException("Invalid path: Attempt to access outside root directory");
    }
    return resolvedPath;
  }
}
