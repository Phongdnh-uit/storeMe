package com.DPhong.storeMe.service.file;

import com.DPhong.storeMe.dto.file.FileRequestDTO;
import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.entity.File;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.FileMapper;
import com.DPhong.storeMe.repository.FileRepository;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.general.FileStorageService;
import com.DPhong.storeMe.service.general.TikaAnalysis;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  private final FolderRepository folderRepository;
  private final FileRepository fileRepository;
  private final FileStorageService fileStorageService;
  private final FileMapper fileMapper;

  @Override
  public FileResponseDTO getFileInfo(Long id) {
    File file = getOrThrowFile(id);
    return fileMapper.entityToResponse(file);
  }

  @Override
  public List<FileResponseDTO> uploadFile(FileRequestDTO fileRequestDTO) {
    Folder parentFolder = null;
    if (fileRequestDTO.getFolderId() != null) {
      parentFolder =
          folderRepository
              .findById(fileRequestDTO.getFolderId())
              .orElseThrow(() -> new ResourceNotFoundException("Folder not found "));
      if (parentFolder.getUser().getId() != SecurityUtils.getCurrentUserId()) {
        throw new BadRequestException("Folder does not belong to user");
      }
    } else {
      parentFolder =
          folderRepository
              .findByUserIdAndParentFolderIdIsNull(SecurityUtils.getCurrentUserId())
              .orElseThrow(() -> new ResourceNotFoundException("Root folder not found"));
    }
    List<FileResponseDTO> result = new ArrayList<>();
    for (MultipartFile fileRequest : fileRequestDTO.getFiles()) {
      File file = new File();
      file.setName(fileRequest.getOriginalFilename());
      file.setSize(fileRequest.getSize());
      file.setFolder(parentFolder);
      file.setUser(parentFolder.getUser());
      file.setPath(fileStorageService.storeFile(parentFolder.getPath(), fileRequest));
      // try with resource to close the stream
      try (InputStream inputStream = fileRequest.getInputStream()) {
        file.setMimeType(TikaAnalysis.getMimeType(inputStream));
        file.setExtension(TikaAnalysis.getExtension(inputStream));
      } catch (Exception e) {
        throw new BadRequestException("Error while uploading file: " + e.getMessage());
      }
      file.setLastAccessed(Instant.now());
      file = fileRepository.save(file);
      result.add(fileMapper.entityToResponse(file));
    }
    return result;
  }

  @Override
  public FileResponseDTO renameFile(Long id, String newName) {
    File file = getOrThrowFile(id);
    file.setName(newName);
    file = fileRepository.save(file);
    return fileMapper.entityToResponse(file);
  }

  @Override
  public Resource serveFile(Long id) {
    File file = getOrThrowFile(id);
    file.setLastAccessed(Instant.now());
    fileRepository.save(file);
    return fileStorageService.loadFileAsResource(file.getPath());
  }

  @Override
  public void deleteFile(Long id) {
    File file = getOrThrowFile(id);
    fileStorageService.deleteFile(file.getPath());
    fileRepository.delete(file);
  }

  @Override
  public void deleteManyFiles(Iterable<Long> ids) {
    List<File> files = fileRepository.findAllById(ids);
    if (files.size() != StreamSupport.stream(ids.spliterator(), false).count()) {
      throw new ResourceNotFoundException("Some files not found");
    }
    for (File file : files) {
      if (file.getUser().getId() != SecurityUtils.getCurrentUserId()) {
        throw new BadRequestException("File does not belong to user");
      }
      fileStorageService.deleteFile(file.getPath());
      fileRepository.delete(file);
    }
  }

  /**
   * @param id the id of the file to be retrieved
   * @apiNote: this method will check if the file exists in the database and if the user has access
   *     to it.
   */
  private File getOrThrowFile(Long id) {
    File file =
        fileRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    if (file.getUser().getId() != SecurityUtils.getCurrentUserId()) {
      throw new BadRequestException("File does not belong to user");
    }
    return file;
  }
}
