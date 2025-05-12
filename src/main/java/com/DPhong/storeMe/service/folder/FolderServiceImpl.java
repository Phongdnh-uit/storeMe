package com.DPhong.storeMe.service.folder;

import com.DPhong.storeMe.constant.ResourceLocation;
import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.FolderMapper;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.GenericService;
import com.DPhong.storeMe.service.general.FileStorageService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl extends GenericService<Folder, FolderRequestDTO, FolderResponseDTO>
    implements FolderService {

  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;

  public FolderServiceImpl(
      FolderRepository repository,
      FolderMapper mapper,
      UserRepository userRepository,
      FileStorageService fileStorageService) {
    super(repository, mapper, Folder.class);
    this.userRepository = userRepository;
    this.fileStorageService = fileStorageService;
  }

  /** this method is used to initialize the USER_STORAGE_ROOT folder for the user */
  @PostConstruct
  private void init() {
    if (!fileStorageService.exists(ResourceLocation.USER_STORAGE_ROOT)) {
      fileStorageService.createFolder("", ResourceLocation.USER_STORAGE_ROOT);
    }
  }

  @Override
  protected void beforeCreateMapper(FolderRequestDTO folderRequestDTO) {
    if (folderRequestDTO.getParentId() != null) {
      if (((FolderRepository) repository)
          .existsByNameAndParentFolderId(
              folderRequestDTO.getName(), folderRequestDTO.getParentId())) {
        throw new ResourceNotFoundException(
            "Folder with name "
                + folderRequestDTO.getName()
                + " already exists in this parent folder");
      }
    }
  }

  @Override
  protected void afterCreateMapper(FolderRequestDTO folderRequestDTO, Folder entity) {
    // get the current user id from security context
    Long userId = SecurityUtils.getCurrentUserId();
    if (userId == null) {
      throw new ResourceNotFoundException("User not found");
    }
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String path = "";

    // check if the parent folder is null or not
    if (folderRequestDTO.getParentId() != null) {
      Folder parentFolder =
          ((FolderRepository) repository)
              .findById(folderRequestDTO.getParentId())
              .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));
      if (user.getId() != parentFolder.getUser().getId()) {
        throw new ResourceNotFoundException("Parent folder not belong to this user");
      }
      entity.setParentFolder(parentFolder);
      path = fileStorageService.createFolder(parentFolder.getPath(), folderRequestDTO.getName());
    } else {
      String userFolderPath =
          fileStorageService.createFolder(ResourceLocation.USER_STORAGE_ROOT, user.getUsername());
      path = fileStorageService.createFolder(userFolderPath, folderRequestDTO.getName());
    }
    entity.setUser(user);
    entity.setPath(path);
  }

  @Override
  public void delete(Long id) {
    Folder folder = findByIdOrThrow(id);
    deleteSubFolder(folder);
    fileStorageService.deleteFolder(folder.getPath());
    repository.delete(folder);
  }

  @Override
  public void deleteAllById(Iterable<Long> ids) {
    List<Folder> folders = repository.findAllById(ids);
    if (folders.size() != StreamSupport.stream(ids.spliterator(), false).count()) {
      throw new ResourceNotFoundException("Some " + Folder.class.getSimpleName() + " not found");
    }
    for (Folder folder : folders) {
      deleteSubFolder(folder);
      fileStorageService.deleteFolder(folder.getPath());
    }
    repository.deleteAll(folders);
  }

  private void deleteSubFolder(Folder folder) {
    for (Folder subFolder : folder.getSubFolders()) {
      deleteSubFolder(subFolder);
    }
    repository.delete(folder);
  }

  @Override
  public void moveFolder(FolderRequestDTO folderRequestDTO) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'moveFolder'");
  }

  @Override
  public void copyFolder(FolderRequestDTO folderRequestDTO) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'copyFolder'");
  }
}
