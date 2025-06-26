package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UploadFileRequestDTO;
import com.DPhong.storeMe.entity.FSNode;
import com.DPhong.storeMe.entity.FileMetadata;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.entity.UserPlan;
import com.DPhong.storeMe.enums.FSType;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.exception.TikaAnalysisException;
import com.DPhong.storeMe.mapper.FSNodeMapper;
import com.DPhong.storeMe.repository.FSNodeRepository;
import com.DPhong.storeMe.repository.FileMetadataRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.general.StorageService;
import com.DPhong.storeMe.service.general.TikaAnalysis;
import com.DPhong.storeMe.service.userPlan.UserPlanService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FSNodeServiceImpl implements FSNodeService {

  private final UserPlanService userPlanService;
  private final UserRepository userRepository;
  private final FSNodeRepository repository;
  private final SecurityUtils securityUtils;
  private final FSNodeMapper fsNodeMapper;
  private final FileMetadataRepository fileMetadataRepository;
  private final StorageService storageService;

  // ============================ GET ALL ITEM IN FOLDER ============================
  /**
   * This method retrieves all file system nodes under a specified parent ID, applying the given
   * specification and pagination.
   *
   * @param parentId The ID of the parent folder. If null, it retrieves nodes at the root level.
   * @param spec The specification to filter the nodes.
   * @param pageable The pagination information.
   * @return A PageResponse containing the list of FSNode objects.
   */
  @Override
  public PageResponse<FSResponseDTO> getAll(
      Long parentId, Specification<FSNode> spec, Pageable pageable) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Build basic spec ----
    Specification<FSNode> baseSpec =
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("user").get("id"), userId),
                builder.isNull(root.get("deletedAt")));
    // 3. ---- Add parentId condition ----
    if (parentId != null) {
      baseSpec =
          baseSpec.and((root, _, builder) -> builder.equal(root.get("parent").get("id"), parentId));
    } else {
      baseSpec = baseSpec.and((root, _, builder) -> builder.isNull(root.get("parent")));
    }
    // 4. ---- Combine base spec with provided spec ----
    Specification<FSNode> combinedSpec = baseSpec.and(spec);
    // 5. ---- Find all items with pagination ----
    Page<FSNode> page = repository.findAll(combinedSpec, pageable);
    // 6. ---- Map to response DTOs ----
    return PageResponse.from(page.map(fsNodeMapper::entityToResponse));
  }

  // ============================ CREATE FOLDER ============================
  @Override
  public FSResponseDTO createFolder(CreateFolderRequestDTO request) {
    // 1. ---- validate ----
    FSNode parentFolder =
        request.getParentId() == null
            ? null
            : validateParentFolder(request.getParentId(), securityUtils.getCurrentUserId());
    List<FSNode> items = getItemInNode(request.getParentId());
    if (items.stream()
        .anyMatch(
            item -> item.getType() == FSType.FOLDER && item.getName().equals(request.getName()))) {
      throw new DataConflictException("Folder with this name already exists.");
    }

    // 2. ---- Object ----
    User user = new User();
    user.setId(securityUtils.getCurrentUserId());

    // 2. ---- Create new folder ----
    FSNode fsNode = new FSNode();
    fsNode.setType(FSType.FOLDER);
    fsNode.setName(request.getName());
    fsNode.setUser(user);
    fsNode.setParent(parentFolder);
    fsNode.setHidden(request.isHidden());
    fsNode.setLocked(request.isLocked());
    fsNode.setLastAccessed(Instant.now());
    if (parentFolder != null) {
      List<Long> ancestor = new ArrayList<>(parentFolder.getAncestor());
      ancestor.add(parentFolder.getId());
      fsNode.setAncestor(ancestor);
    }
    return fsNodeMapper.entityToResponse(repository.save(fsNode));
  }

  // ============================ UPLOAD FILES ============================
  @Transactional
  @Override
  public List<FSResponseDTO> uploadFiles(UploadFileRequestDTO request) {
    // 1. ---- Validate ----
    Long userId = securityUtils.getCurrentUserId();
    // 1.1 ---- Check if user has a plan ----
    UserPlan userPlan =
        userPlanService
            .getCurrentUserPlanIfExists(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("User has not subscribed to any plan."));
    User user = userPlan.getUser();
    // 1.2 ---- Check if user has enough storage ----
    Long totalSize = request.getFiles().stream().mapToLong(MultipartFile::getSize).sum();
    if (totalSize + user.getTotalUsage() > userPlan.getStoragePlan().getStorageLimit()) {
      throw new DataConflictException(
          "Total file size exceeds the storage limit of the current plan.");
    }
    FSNode parentFolder =
        request.getParentId() == null
            ? null
            : validateParentFolder(request.getParentId(), securityUtils.getCurrentUserId());
    List<FSNode> items = getItemInNode(request.getParentId());
    Set<String> existingFileNames =
        items.stream()
            .filter(item -> item.getType() == FSType.FILE)
            .map(FSNode::getName)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
    Instant now = Instant.now();
    // 2. ---- Save each file ----
    List<FSResponseDTO> responseList = new ArrayList<>();
    for (MultipartFile file : request.getFiles()) {
      FSNode fileNode = new FSNode();
      fileNode.setType(FSType.FILE);
      fileNode.setUser(user);
      fileNode.setSize(file.getSize());
      // PROBLEM: fileName could be existing in the folder
      // 3. ---- Rename file if exists ----
      fileNode.setName(getUniqueFileName(file.getOriginalFilename(), existingFileNames));
      // Set additional properties
      fileNode.setParent(parentFolder);
      fileNode.setLastAccessed(now);
      if (parentFolder != null) {
        List<Long> ancestor = new ArrayList<>(parentFolder.getAncestor());
        ancestor.add(parentFolder.getId());
        fileNode.setAncestor(ancestor);
      }
      fileNode = repository.save(fileNode);
      responseList.add(fsNodeMapper.entityToResponse(fileNode));
      // 4. ---- Add metadata ----
      FileMetadata metadata = new FileMetadata();
      try (InputStream inputStream = file.getInputStream()) {
        metadata.setMimeType(TikaAnalysis.getMimeType(inputStream));
        metadata.setExtension(TikaAnalysis.getExtension(inputStream));
      } catch (IOException e) {
        throw new TikaAnalysisException(e.getMessage());
      }
      metadata.setFile(fileNode);
      String blobKey = UUID.randomUUID().toString();
      metadata.setBlobKey(blobKey);
      fileMetadataRepository.save(metadata);
      // 5. ---- Save file to blob storage ----
      String path = generateBlobPath(blobKey);
      storageService.storeFile(path, file);
    }
    // 6. ---- Update user storage usage ----
    user.setTotalUsage(user.getTotalUsage() + totalSize);
    userRepository.save(user);
    return responseList;
  }

  // ============================ UPDATE FSNODE: RENAME, MOVE, COPY ============================
  @Override
  public FSResponseDTO update(UpdateFSNodeRequestDTO request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  // ============================ DELETE ITEM ============================
  @Transactional
  @Override
  public void delete(Long id) {
    Instant now = Instant.now();
    // 1. ---- Delete current node ----
    FSNode item =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.equal(root.get("user").get("id"), securityUtils.getCurrentUserId()),
                        builder.isNull(root.get("deletedAt"))))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Item not found or does not belong to current user."));
    item.setDeletedAt(now);
    item.setLastAccessed(now);
    // Set parent to null to make it a root item in trash
    item.setParent(null);
    // Keep ancestor unchanged for rolling back, due to set parent to null
    repository.save(item);
    // 2. ---- Delete sub node ----
    List<FSNode> subNodes =
        repository.findAll(
            (root, _, builder) ->
                builder.and(
                    builder.isMember(id, root.get("ancestor")),
                    builder.equal(root.get("user").get("id"), securityUtils.getCurrentUserId()),
                    builder.isNull(root.get("deletedAt"))));
    subNodes.stream()
        .forEach(
            subNode -> {
              subNode.setDeletedAt(now);
              subNode.setLastAccessed(now);
            });
    repository.saveAll(subNodes);
  }

  // ============================ DELETE MANY ITEMS ============================
  @Transactional
  @Override
  public void deleteMany(List<Long> ids) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Find all items by ids and userId ----
    List<FSNode> items =
        repository.findAll(
            (root, _, builder) ->
                builder.and(
                    root.get("id").in(ids),
                    builder.equal(root.get("user").get("id"), userId),
                    builder.isNull(root.get("deletedAt"))));
    // 3. ---- Check if all items exist ----
    if (items.size() != ids.size()) {
      throw new ResourceNotFoundException("Some items not found or do not belong to current user.");
    }
    // 4. ---- Check if child node include ----
    Set<Long> idSet = new HashSet<>(ids);
    items.removeIf(item -> item.getAncestor().stream().anyMatch(idSet::contains));
    // 5. ---- Set deletedAt for each item ----
    Instant now = Instant.now();
    items.forEach(
        item -> {
          item.setDeletedAt(now);
          item.setLastAccessed(now);
          // Set parent to null to make it a root item in trash
          item.setParent(null);
          // Keep ancestor unchanged for rolling back, due to set parent to null
        });
    // 6. ---- Save all items ----
    repository.saveAll(items);
    // 7. ---- Delete sub node ----
    List<FSNode> subNodes =
        repository.findAll(
            (root, _, builder) ->
                builder.and(
                    root.get("ancestor").in(ids),
                    builder.equal(root.get("user").get("id"), userId),
                    builder.isNull(root.get("deletedAt"))));
    subNodes.stream()
        .forEach(
            subNode -> {
              subNode.setDeletedAt(now);
              subNode.setLastAccessed(now);
            });
    repository.saveAll(subNodes);
  }

  // ============================ HELPER METHODS ============================
  /**
   * This method retrieves all file system nodes under the file system node. if parentId is null, it
   * retrieves all items in the root folder.
   *
   * @param parentId The ID of the parent folder. If null, it retrieves nodes at the root level.
   */
  private List<FSNode> getItemInNode(Long parentId) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Build basic spec ----
    Specification<FSNode> spec =
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("user").get("id"), userId),
                builder.isNull(root.get("deletedAt")));
    // 3. ---- Add parentId condition ----
    if (parentId != null) {
      spec = spec.and((root, _, builder) -> builder.equal(root.get("parent").get("id"), parentId));
    } else {
      spec = spec.and((root, _, builder) -> builder.isNull(root.get("parent")));
    }
    return repository.findAll(spec);
  }

  private FSNode validateParentFolder(Long parentId, Long userId) {
    return repository
        .findOne(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), parentId),
                    builder.equal(root.get("user").get("id"), userId),
                    builder.equal(root.get("type"), FSType.FOLDER),
                    builder.isNull(root.get("deletedAt"))))
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Parent folder not found or does not belong to current user."));
  }

  // ============================ HELPER ============================
  private String getUniqueFileName(String originalFileName, Set<String> existingFileNames) {
    if (originalFileName == null) {
      originalFileName = UUID.randomUUID().toString();
    }

    String base = originalFileName;
    String ext = "";

    int dot = originalFileName.lastIndexOf('.');
    if (dot != -1) {
      base = originalFileName.substring(0, dot);
      ext = originalFileName.substring(dot);
    }

    String candidate = base + ext;
    int i = 1;
    while (existingFileNames.contains(candidate)) {
      candidate = base + " (" + i + ")" + ext;
      i++;
    }

    existingFileNames.add(candidate);
    return candidate;
  }

  private String generateBlobPath(String blobKey) {
    return blobKey.substring(0, 2)
        + "/"
        + blobKey.substring(2, 4)
        + "/"
        + blobKey.substring(4, 6)
        + "/"
        + blobKey.substring(6);
  }
}
