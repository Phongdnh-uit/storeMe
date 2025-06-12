package com.DPhong.storeMe.service.folder;

import com.DPhong.storeMe.dto.PageResponse;
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
import com.DPhong.storeMe.validator.folder.FolderValidator;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl extends GenericService<Folder, FolderRequestDTO, FolderResponseDTO>
    implements FolderService {

  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;
  private final FolderValidator folderValidator;

  public FolderServiceImpl(
      FolderRepository repository,
      FolderMapper mapper,
      UserRepository userRepository,
      SecurityUtils securityUtils,
      FolderValidator folderValidator) {
    super(repository, mapper, Folder.class);
    this.userRepository = userRepository;
    this.securityUtils = securityUtils;
    this.folderValidator = folderValidator;
  }

  @Override
  public PageResponse<FolderResponseDTO> findAll(
      Specification<Folder> specification, Pageable pageable) {
    Folder rootFolder =
        ((FolderRepository) repository)
            .findByUserIdAndParentFolderIdIsNull(securityUtils.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Root folder have not been created"));
    Specification<Folder> spec =
        (root, _, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get("parentFolder").get("id"), rootFolder.getId()),
                criteriaBuilder.equal(
                    root.get("user").get("id"), securityUtils.getCurrentUserId()));
    spec.and(specification);
    Page<Folder> page = repository.findAll(spec, pageable);
    return new PageResponse<FolderResponseDTO>()
        .setContent(page.getContent().stream().map(mapper::entityToResponse).toList())
        .setNumber(page.getNumber())
        .setSize(page.getSize())
        .setTotalElements(page.getTotalElements())
        .setTotalPages(page.getTotalPages());
  }

  @Override
  protected void beforeCreateMapper(FolderRequestDTO folderRequestDTO) {
    // Validate the folder request before creating
    folderValidator.validateCreate(folderRequestDTO);
  }

  @Override
  protected void afterCreateMapper(FolderRequestDTO folderRequestDTO, Folder entity) {
    // get the current user id from security context
    Long userId = securityUtils.getCurrentUserId();
    if (userId == null) {
      throw new ResourceNotFoundException("User not found");
    }
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String path = "";

    Folder parentFolder = null;

    // check if the parent folder is null or not
    if (folderRequestDTO.getParentId() != null) {
      parentFolder =
          ((FolderRepository) repository)
              .findById(folderRequestDTO.getParentId())
              .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));
      if (parentFolder.getUser().getId() != userId) {
        throw new ResourceNotFoundException("Parent folder not belong to this user");
      }
    } else {
      parentFolder =
          ((FolderRepository) repository)
              .findByUserIdAndParentFolderIdIsNull(userId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Root folder have not been created"));
    }
    path = fileStorageService.createFolder(parentFolder.getPath(), folderRequestDTO.getName());
    entity.setParentFolder(parentFolder);
    entity.setUser(user);
    entity.setPath(path);
  }

  @Override
  protected void beforeUpdateMapper(Long id, FolderRequestDTO request, Folder old) {
    fileStorageService.renameFolder(old.getPath(), old.getName(), request.getName());
  }

  @Override
  public void delete(Long id) {
    Folder folder = findByIdOrThrow(id);
    if (folder.getParentFolder() == null) {
      throw new ResourceNotFoundException("Root folder can not be deleted");
    }
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
      if (folder.getParentFolder() == null) {
        throw new ResourceNotFoundException("Root folder can not be deleted");
      }
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
