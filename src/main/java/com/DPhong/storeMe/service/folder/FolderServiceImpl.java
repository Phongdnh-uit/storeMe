package com.DPhong.storeMe.service.folder;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.File;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.enums.FolderType;
import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.mapper.FolderMapper;
import com.DPhong.storeMe.repository.FileRepository;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.GenericService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FolderServiceImpl extends GenericService<Folder, FolderRequestDTO, FolderResponseDTO>
    implements FolderService {

  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;
  private final FileRepository fileRepository;

  public FolderServiceImpl(
      FolderRepository repository,
      FolderMapper mapper,
      UserRepository userRepository,
      SecurityUtils securityUtils,
      FileRepository fileRepository) {
    super(repository, mapper, Folder.class);
    this.userRepository = userRepository;
    this.securityUtils = securityUtils;
    this.fileRepository = fileRepository;
  }

  @Override
  public PageResponse<FolderResponseDTO> findAll(
      Specification<Folder> specification, Pageable pageable) {
    Specification<Folder> spec =
        (root, _, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get("type"), FolderType.NORMAL),
                criteriaBuilder.equal(root.get("isLocked"), false),
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

  // ============================ CREATE ============================
  @Override
  public FolderResponseDTO create(FolderRequestDTO request) {

    // 1. ---- validate ----
    Long currentUserId = securityUtils.getCurrentUserId();

    Folder parentFolder = getParentFolder(request.getParentId(), currentUserId);

    /** Check if the folder name already exists in the parent folder */
    if (parentFolder.getSubFolders().stream()
        .anyMatch(f -> f.getName().equals(request.getName()))) {
      throw new DataConflictException(
          "Folder name already exists in the parent folder: " + request.getName());
    }

    // 2. ---- mapper ----
    Folder entity = mapper.requestToEntity(request);

    // 3. ---- set properties ----
    entity.setParentFolder(parentFolder);
    entity.setUser(
        userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new IllegalStateException("User id must not be null")));

    // clone the parent folder's ancestor list
    List<Long> ancestor = new ArrayList<>(parentFolder.getAncestor());
    ancestor.add(parentFolder.getId());
    entity.setAncestor(ancestor);

    entity = repository.save(entity);

    return mapper.entityToResponse(entity);
  }

  // ============================ UPDATE: RENAME, MOVE, COPY ============================
  @Override
  public FolderResponseDTO update(Long id, UpdateFSNodeRequestDTO request) {

    // 1. ---- validate ----
    Long currentUserId = securityUtils.getCurrentUserId();

    Folder folder = findByIdOrThrow(id);
    if (folder.getUser().getId() != currentUserId) {
      throw new BadRequestException("Folder does not belong to this user");
    }

    if (folder.isLocked()) {
      throw new BadRequestException("Folder is locked and cannot be modified");
    }

    Folder parentFolder = getParentFolder(request.getParentId(), currentUserId);

    /* Check if the folder exists and belongs to the current user */
    if (parentFolder.getSubFolders().stream()
        .anyMatch(f -> f.getName().equals(request.getName()) && !f.getId().equals(id))) {
      throw new DataConflictException(
          "Folder name already exists in the parent folder: " + request.getName());
    }

    // 2. ---- update properties ----

    // PROBLEM: ancestor must be updated when moving a folder
    switch (request.getAction()) {
      case RENAME:
        folder.setName(request.getName());
        folder = repository.save(folder);
        return mapper.entityToResponse(folder);
      case MOVE:
        // case test: /test/uit -> /test/uit/example
        if (parentFolder.getAncestor().contains(folder.getId())) {
          throw new BadRequestException("Cannot move a folder into its own subfolder");
        }
        folder.setParentFolder(parentFolder);
        // Update the ancestor of the folder
        updateAncestorList(folder.getId(), parentFolder.getAncestor());
        folder = repository.save(folder);
        return mapper.entityToResponse(folder);
      case COPY:
      // Create a copy of the folder
      default:
        throw new BadRequestException("Invalid folder action");
    }
  }

  // ============================ DELETE FOLDER ============================
  @Override
  public void delete(Long id) {
    // 1. ---- validate ----
    Long currentUserId = securityUtils.getCurrentUserId();

    Folder trashFolder =
        ((FolderRepository) repository)
            .findByUserIdAndType(currentUserId, FolderType.TRASH)
            .orElseThrow(() -> new IllegalStateException("Trash folder have not been created"));

    Folder folder = findByIdOrThrow(id);

    if (folder.getUser().getId() != currentUserId) {
      throw new BadRequestException("Folder not belong to this user");
    }

    // check if folder is locked
    if (folder.isLocked()) {
      throw new BadRequestException("Folder is locked and cannot be deleted");
    }

    // 2. ---- add to trash ----
    folder.setParentFolder(trashFolder);
    folder.setDeletedAt(Instant.now());
    repository.save(folder);
  }

  // ============================ DELETE ALL FOLDER ============================
  @Override
  public void deleteAllById(Iterable<Long> ids) {
    // 1. ---- validate ----
    Long currentUserId = securityUtils.getCurrentUserId();
    Folder trashFolder =
        ((FolderRepository) repository)
            .findByUserIdAndType(currentUserId, FolderType.TRASH)
            .orElseThrow(() -> new IllegalStateException("Trash folder have not been created"));

    // 2. ---- add to trash ----
    List<Folder> deletableFolders =
        repository.findAllById(ids).stream()
            .filter(folder -> !folder.isLocked() && folder.getUser().getId().equals(currentUserId))
            .map(
                folder -> {
                  folder.setDeletedAt(Instant.now());
                  folder.setParentFolder(trashFolder);
                  return folder;
                })
            .toList();
    repository.saveAll(deletableFolders);
  }

  // ============================ CLEAN TRASH ============================
  @Override
  public void cleanTrash() {
    Folder trashFolder =
        ((FolderRepository) repository)
            .findByUserIdAndType(securityUtils.getCurrentUserId(), FolderType.TRASH)
            .orElseThrow(() -> new IllegalStateException("Trash folder have not been created"));
    repository.deleteAll(trashFolder.getSubFolders());
  }

  // ============================ CRON JOB CLEAN TRASH ============================
  @Override
  public void cronJobCleanTrash() {
    Folder trashFolder =
        ((FolderRepository) repository)
            .findByUserIdAndType(securityUtils.getCurrentUserId(), FolderType.TRASH)
            .orElseThrow(() -> new IllegalStateException("Trash folder have not been created"));
    List<Folder> deletableFolders =
        trashFolder.getSubFolders().stream()
            .filter(f -> f.getDeletedAt().isBefore(Instant.now()))
            .toList();
    repository.deleteAll(deletableFolders);
  }

  // ============================ HELPER ============================
  private Folder getParentFolder(Long parentFolderId, Long currentUserId) {
    return parentFolderId == null || parentFolderId == 0
        ? ((FolderRepository) repository)
            .findByUserIdAndType(currentUserId, FolderType.USERROOT)
            .orElseThrow(() -> new IllegalArgumentException("User root folder not found"))
        : repository
            .findById(parentFolderId)
            .filter(folder -> folder.getUser().getId().equals(currentUserId))
            .orElseThrow(() -> new BadRequestException("Parent folder not found"));
  }

  @Transactional
  private void updateAncestorList(Long ancestorId, List<Long> replaceIds) {
    List<Folder> folders = ((FolderRepository) repository).findByAncestorContain(ancestorId);
    List<File> files = fileRepository.findByAncestorContain(ancestorId);

    for (Folder folder : folders) {
      List<Long> ancestor = folder.getAncestor();
      int replaceIndex = ancestor.indexOf(ancestorId);
      ancestor.subList(0, replaceIndex).clear();
      ancestor.addAll(0, replaceIds);
    }
    for (File file : files) {
      List<Long> ancestor = file.getAncestor();
      int replaceIndex = ancestor.indexOf(ancestorId);
      ancestor.subList(0, replaceIndex).clear();
      ancestor.addAll(0, replaceIds);
    }
    fileRepository.saveAll(files);
    repository.saveAll(folders);
  }
}
