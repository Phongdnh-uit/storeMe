package com.DPhong.storeMe.service.file;

import com.DPhong.storeMe.dto.file.FileRequestDTO;
import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.entity.File;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.entity.UserPlan;
import com.DPhong.storeMe.enums.FolderType;
import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.FileMapper;
import com.DPhong.storeMe.repository.FileRepository;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.UserPlanRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.general.StorageService;
import com.DPhong.storeMe.service.general.TikaAnalysis;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  private final FolderRepository folderRepository;
  private final UserPlanRepository userPlanRepository;
  private final FileRepository fileRepository;
  private final FileMapper fileMapper;
  private final UserRepository userRepository;
  private final StorageService storageService;
  private final SecurityUtils securityUtils;

  @Override
  public FileResponseDTO getFileInfo(Long id) {
    File file = getOrThrowFile(id);
    return fileMapper.entityToResponse(file);
  }

  // ============================ CREATE ============================
  @Override
  public List<FileResponseDTO> uploadFile(FileRequestDTO fileRequestDTO) {
    // 1. ---- validate ----
    Folder parentFolder = findParentFolder(fileRequestDTO.getFolderId());

    User user = parentFolder.getUser();

    UserPlan currentPlan =
        userPlanRepository
            .findByUserIdAndIsActiveTrue(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not register any plan"));
    Long totalSize = 0L;

    // 2. ---- check if the user has enough space in the plan ----
    for (MultipartFile fileRequest : fileRequestDTO.getFiles()) {
      totalSize += fileRequest.getSize();
    }
    if (user.getTotalUsage() + totalSize >= currentPlan.getStoragePlan().getStorageLimit()) {
      throw new BadRequestException("Not enough space in plan");
    }

    // 3. ---- save the files ----
    List<FileResponseDTO> result = new ArrayList<>();
    for (MultipartFile fileRequest : fileRequestDTO.getFiles()) {
      File file = new File();
      file.setName(fileRequest.getOriginalFilename());
      file.setSize(fileRequest.getSize());
      file.setFolder(parentFolder);
      file.setUser(user);
      List<Long> ancestor = new ArrayList<>(parentFolder.getAncestor());
      ancestor.add(parentFolder.getId());
      file.setAncestor(ancestor);
      // parse the file and set mime type and extension
      try (InputStream inputStream = fileRequest.getInputStream()) {
        file.setMimeType(TikaAnalysis.getMimeType(inputStream));
        file.setExtension(TikaAnalysis.getExtension(inputStream));
      } catch (Exception e) {
        throw new BadRequestException("Error while uploading file: " + e.getMessage());
      }
      // save the file to storage
      String blobKey = UUID.randomUUID().toString();
      String storePath =
          blobKey.substring(0, 2) + "/" + blobKey.substring(3, 5) + "/" + blobKey.substring(6, 8);
      storageService.storeFile(storePath, fileRequest);
      file.setBlobKey(blobKey);
      file.setLastAccessed(Instant.now());
      file = fileRepository.save(file);
      result.add(fileMapper.entityToResponse(file));
      user.setTotalUsage(user.getTotalUsage() + file.getSize());
    }
    userRepository.save(user);
    return result;
  }

  // ============================ UPDATE: RENAME, COPY, MOVE ============================
  @Override
  public FileResponseDTO update(Long id, UpdateFSNodeRequestDTO request) {
    // 1. ---- validate ----
    File file = getOrThrowFile(id);

    // 2. ---- update the file ----
    switch (request.getAction()) {
      case RENAME:
        if (file.getFolder().getFiles().stream()
            .anyMatch(f -> f.getName().equals(request.getName()) && !f.getId().equals(id))) {
          throw new BadRequestException("File with the same name already exists in the folder");
        }
        file.setName(request.getName());
        file.setLastAccessed(Instant.now());
        fileRepository.save(file);
        return fileMapper.entityToResponse(file);
      case MOVE:
        Folder newFolder = findParentFolder(request.getParentId());
        if (newFolder.getFiles().stream()
            .anyMatch(f -> f.getName().equals(file.getName()) && !f.getId().equals(id))) {
          throw new BadRequestException("File with the same name already exists in the folder");
        }
        file.setFolder(newFolder);
        file.getAncestor().removeLast();
        file.getAncestor().add(newFolder.getId());
        file.setLastAccessed(Instant.now());
        fileRepository.save(file);
        return fileMapper.entityToResponse(file);
      case COPY:
        newFolder = findParentFolder(request.getParentId());
        if (file.getFolder().getId().equals(newFolder.getId())) {
          throw new BadRequestException("Cannot copy file to the same folder");
        }
        if (newFolder.getFiles().stream()
            .anyMatch(f -> f.getName().equals(file.getName()) && !f.getId().equals(id))) {
          throw new BadRequestException("File with the same name already exists in the folder");
        }
        File copiedFile = new File();
        copiedFile.setName(file.getName());
        copiedFile.setSize(file.getSize());
        copiedFile.setFolder(newFolder);
        copiedFile.setUser(file.getUser());
        copiedFile.setMimeType(file.getMimeType());
        copiedFile.setExtension(file.getExtension());
        copiedFile.setLastAccessed(Instant.now());
        String blobKey = UUID.randomUUID().toString();
        String storePath =
            blobKey.substring(0, 2) + "/" + blobKey.substring(3, 5) + "/" + blobKey.substring(6, 8);
        storageService.copyFile(file.getBlobKey(), storePath);
        copiedFile.setBlobKey(blobKey);
        List<Long> ancestor = new ArrayList<>(newFolder.getAncestor());
        ancestor.removeLast();
        ancestor.add(newFolder.getId());
        copiedFile.setAncestor(ancestor);
        copiedFile = fileRepository.save(copiedFile);
        // 1. ---- Update User ----
        User user = copiedFile.getUser();
        user.setTotalUsage(user.getTotalUsage() + copiedFile.getSize());
        userRepository.save(user);
        return fileMapper.entityToResponse(copiedFile);
      default:
        throw new BadRequestException("Invalid action for file update");
    }
  }

  // ============================ SERVE FILE ============================
  @Override
  public Resource serveFile(Long id) {
    File file = getOrThrowFile(id);
    file.setLastAccessed(Instant.now());
    fileRepository.save(file);
    String blobKey = file.getBlobKey();
    String storePath =
        blobKey.substring(0, 2)
            + "/"
            + blobKey.substring(3, 5)
            + "/"
            + blobKey.substring(6, 8)
            + "/"
            + blobKey.substring(9);
    return storageService.loadFileAsResource(storePath);
  }

  // ============================ DELETE FILE: MOVE TO TRASH ============================
  @Override
  public void deleteFile(Long id) {
    // 1. ---- validate ----
    File file = getOrThrowFile(id);
    Folder trashFolder =
        folderRepository
            .findByUserIdAndType(file.getUser().getId(), FolderType.TRASH)
            .orElseThrow(() -> new ResourceNotFoundException("Trash folder not initialized"));
    file.setFolder(trashFolder);
    file.setDeletedAt(Instant.now());
    fileRepository.save(file);
  }

  // ============================ DELETE MANY FILES: MOVE TO TRASH ============================
  @Override
  public void deleteManyFiles(Iterable<Long> ids) {
    Long currentUserId = securityUtils.getCurrentUserId();
    Folder trashFolder =
        folderRepository
            .findByUserIdAndType(currentUserId, FolderType.TRASH)
            .orElseThrow(() -> new ResourceNotFoundException("Trash folder not initialized"));
    List<File> deletableFiles =
        fileRepository.findAllById(ids).stream()
            .filter(f -> f.getUser().getId().equals(currentUserId))
            .map(
                file -> {
                  file.setFolder(trashFolder);
                  file.setDeletedAt(Instant.now());
                  return file;
                })
            .toList();
    fileRepository.saveAll(deletableFiles);
  }

  // ============================ HELPER ============================
  /**
   * @param id the id of the file to be retrieved
   * @apiNote: this method will check if the file exists in the database and if the user has access
   *     to it.
   */
  private File getOrThrowFile(Long id) {
    File file =
        fileRepository
            .findById(id)
            .filter(f -> f.getUser().getId().equals(securityUtils.getCurrentUserId()))
            .orElseThrow(() -> new ResourceNotFoundException("File not found"));
    return file;
  }

  private Folder findParentFolder(Long folderId) {
    Long currentUserId = securityUtils.getCurrentUserId();
    return folderId == null || folderId == 0
        ? folderRepository
            .findByUserIdAndType(currentUserId, FolderType.USERROOT)
            .orElseThrow(() -> new IllegalStateException("User root folder not initialized"))
        : folderRepository
            .findById(folderId)
            .filter(folder -> folder.getUser().getId().equals(currentUserId))
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));
  }
}
