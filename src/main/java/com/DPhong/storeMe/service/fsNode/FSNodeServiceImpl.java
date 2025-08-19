package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.TransferFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UploadFileRequestDTO;
import com.DPhong.storeMe.entity.FSNode;
import com.DPhong.storeMe.entity.FileMetadata;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.entity.UserPlan;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.FSType;
import com.DPhong.storeMe.exception.ApiException;
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
import jakarta.persistence.criteria.Expression;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tika.io.TikaInputStream;
import org.springframework.core.io.Resource;
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
  private final FSPermissionService fsPermissionService;

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
        (root, _, builder) -> builder.and(builder.isNull(root.get("deletedAt")));
    // 3. ---- Add parentId condition ----
    if (parentId != null) {
      FSNode parentFolder = getParentFolder(parentId);
      // 3.1 ---- Check if user has permission to access the parent folder ----
      fsPermissionService.checkCanRead(userId, parentFolder);
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

  // ============================ GET ITEM BY ID ============================
  @Override
  public FSResponseDTO getById(Long id) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Find item by id and userId ----
    FSNode fsNode =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id), builder.isNull(root.get("deletedAt"))))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Item not found or does not belong to current user."));
    // 2.1 ---- Check if user has permission to access the item ----
    fsPermissionService.checkCanRead(userId, fsNode);
    // 3. ---- Map to response DTO ----
    return fsNodeMapper.entityToResponse(fsNode);
  }

  // ============================ CREATE FOLDER ============================
  @Override
  public FSResponseDTO createFolder(CreateFolderRequestDTO request) {
    // 1. ---- validate ----
    FSNode parentFolder =
        request.getParentId() == null ? null : getParentFolder(request.getParentId());
    // 1.1 ---- Check if user has permission to create folder in parent folder ----
    if (parentFolder != null) {
      fsPermissionService.checkCanWrite(securityUtils.getCurrentUserId(), parentFolder);
    }
    List<FSNode> items = getItemInNode(request.getParentId());
    if (items.stream()
        .anyMatch(
            item -> item.getType() == FSType.FOLDER && item.getName().equals(request.getName()))) {
      throw new DataConflictException("Folder with this name already exists.");
    }

    // 2. ---- Object ----
    User user =
        userRepository
            .findById(securityUtils.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

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
        request.getParentId() == null ? null : getParentFolder(request.getParentId());
    // 1.3 ---- Check if user has permission to upload files in parent folder ----
    if (parentFolder != null) {
      fsPermissionService.checkCanWrite(userId, parentFolder);
    }
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
      // 4. ---- Add metadata ----
      FileMetadata metadata = new FileMetadata();
      try (TikaInputStream inputStream = TikaInputStream.get(file.getInputStream())) {
        metadata.setMimeType(TikaAnalysis.getMimeType(inputStream));
        metadata.setExtension(TikaAnalysis.getExtension(inputStream));
      } catch (IOException e) {
        throw new TikaAnalysisException(e.getMessage());
      }
      metadata.setFile(fileNode);
      String blobKey = UUID.randomUUID().toString();
      metadata.setBlobKey(blobKey);
      metadata = fileMetadataRepository.save(metadata);
      // 5. ---- Save file to blob storage ----
      String path = generateBlobPath(blobKey);
      storageService.storeFile(path, file);
      fileNode.setFileMetadata(metadata);
      responseList.add(fsNodeMapper.entityToResponse(fileNode));
    }
    // 6. ---- Update user storage usage ----
    user.setTotalUsage(user.getTotalUsage() + totalSize);
    userRepository.save(user);
    return responseList;
  }

  // ============================ GET FILE ============================
  @Override
  public Resource getFile(Long id) {
    // 1. ---- Get file ----
    FSNode fsNode =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.equal(root.get("type"), FSType.FILE),
                        builder.isNull(root.get("deletedAt"))))
            .orElseThrow(() -> new ResourceNotFoundException());

    // 2. ---- Check if user has permission to access the file ----
    fsPermissionService.checkCanRead(securityUtils.getCurrentUserId(), fsNode);

    FileMetadata fileMetadata = fsNode.getFileMetadata();
    if (fileMetadata == null) {
      throw new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION);
    }

    String blobKey = fileMetadata.getBlobKey();
    String path = generateBlobPath(blobKey);
    Resource resource = storageService.loadFileAsResource(path);
    return resource;
  }

  // ============================ UPDATE FSNODE: UPDATE METADATA ============================
  @Transactional
  @Override
  public FSResponseDTO update(Long id, UpdateFSNodeRequestDTO request) {
    // 1. ---- Validate ----
    FSNode fsNode = getItemById(id);
    // 1.1 ---- Check if user has permission to update the item ----
    fsPermissionService.checkCanWrite(securityUtils.getCurrentUserId(), fsNode);
    //  2. ---- Handle in each case ----
    List<FSNode> itemsInNode =
        getItemInNode(fsNode.getParent() == null ? null : fsNode.getParent().getId());
    if (itemsInNode.stream()
        .anyMatch(item -> item.getName().equals(request.getName()) && !item.getId().equals(id))) {
      throw new DataConflictException("File system node with this name already exists.");
    }
    fsNode.setName(request.getName());
    // 2.1 ---- Update hidden and locked status if provided - Only owner can change this ----
    if (fsNode.getUser().getId().equals(securityUtils.getCurrentUserId())) {
      fsNode.setHidden(request.isHidden());
      fsNode.setLocked(request.isLocked());
    }
    fsNode.setLastAccessed(Instant.now());
    return fsNodeMapper.entityToResponse(repository.save(fsNode));
  }

  // ============================ TRANSFER FSNODE: MOVE, COPY ============================
  @Transactional
  @Override
  public FSResponseDTO transfer(Long id, TransferFSNodeRequestDTO request) {
    // 1. ---- Validate ----
    FSNode fsNode = getItemById(id);
    // 1.1 ---- Check if user has permission to update the item ----
    fsPermissionService.checkCanWrite(securityUtils.getCurrentUserId(), fsNode);
    FSNode destination =
        request.getDestinationId() == null ? null : getParentFolder(request.getDestinationId());
    // 1.2 ---- Check if user has permission to move/copy the item to destination folder ----
    fsPermissionService.checkCanWrite(securityUtils.getCurrentUserId(), fsNode);
    fsPermissionService.checkCanWrite(securityUtils.getCurrentUserId(), destination);
    List<FSNode> itemsInTarget = getItemInNode(request.getDestinationId());
    if (itemsInTarget.stream()
        .anyMatch(
            item ->
                item.getName().equals(fsNode.getName()) && !item.getId().equals(fsNode.getId()))) {
      throw new DataConflictException("File system node with this name already exists.");
    }
    final FSNode resultNode;
    //  2. ---- Handle in each case ----
    switch (request.getAction()) {
      case MOVE:
        //  ---- Only move in same store space ----
        if ((destination == null && fsNode.getUser().getId() != securityUtils.getCurrentUserId())
            || (destination != null && destination.getUser().getId() != fsNode.getUser().getId())) {
          throw new DataConflictException("Cannot move item to another workspace.");
        }
        resultNode = moveItem(fsNode, destination);
        break;
      case COPY:
        resultNode = copy(fsNode, destination);
        break;
      default:
        throw new UnsupportedOperationException(
            "Action " + request.getAction() + " is not supported yet.");
    }
    return fsNodeMapper.entityToResponse(resultNode);
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
                        builder.equal(root.get("id"), id), builder.isNull(root.get("deletedAt"))))
            .orElseThrow(() -> new ResourceNotFoundException("Item not found."));
    // 1.1 ---- Check if user has permission to delete the item ----
    fsPermissionService.checkCanDelete(securityUtils.getCurrentUserId(), item);

    item.setDeletedAt(now);
    item.setLastAccessed(now);
    // Set parent to null to make it a root item in trash
    item.setParent(null);
    // Keep ancestor unchanged for rolling back, due to set parent to null
    repository.save(item);
    // 2. ---- Delete sub node ----
    List<FSNode> subNodes =
        repository.findAll(
            (root, _, builder) -> {
              Expression<Integer> pos =
                  builder.function(
                      "array_position", Integer.class, root.get("ancestor"), builder.literal(id));
              return builder.and(
                  builder.greaterThan(pos, 0), builder.isNull(root.get("deletedAt")));
            });
    subNodes.stream()
        .forEach(
            subNode -> {
              subNode.setDeletedAt(now);
              subNode.setLastAccessed(now);
            });
    repository.saveAll(subNodes);
  }

  // ============================ GET TRASH ============================
  @Override
  public PageResponse<FSResponseDTO> getTrash(Specification<FSNode> spec, Pageable pageable) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Build basic spec ----
    Specification<FSNode> baseSpec =
        (root, _, builder) ->
            builder.and(
                builder.isNull(root.get("parent")), // Only get root items in trash
                builder.equal(root.get("user").get("id"), userId),
                builder.isNotNull(root.get("deletedAt")));
    // 3. ---- Combine base spec with provided spec ----
    Specification<FSNode> combinedSpec = baseSpec.and(spec);
    // 4. ---- Find all items in trash with pagination ----
    Page<FSNode> page = repository.findAll(combinedSpec, pageable);
    // 5. ---- Map to response DTOs ----
    return PageResponse.from(page.map(fsNodeMapper::entityToResponse));
  }

  // ============================ RESTORE ============================
  @Override
  public void restore(Long id) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Find item by id and userId ----
    FSNode fsNode =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.equal(root.get("user").get("id"), userId),
                        builder.isNotNull(root.get("deletedAt"))))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Item not found or does not belong to current user."));
    // 3. ---- Check if parent is exists through ancestor ----
    if (!fsNode.getAncestor().isEmpty()) {
      Long parentId = fsNode.getAncestor().getLast();
      FSNode parent = getParentFolder(parentId);
      fsNode.setParent(parent);
    }
    // 3. ---- Restore item by setting deletedAt to null and lastAccessed to now ----
    fsNode.setDeletedAt(null);
    fsNode.setLastAccessed(Instant.now());
    repository.save(fsNode);
  }

  // ============================ DELETE PERMANENTLY ============================
  @Transactional
  @Override
  public void deletePermanently(Long id) {
    // 1. ---- Get current user id ----
    Long userId = securityUtils.getCurrentUserId();
    // 2. ---- Find item by id and userId ----
    FSNode fsNode =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.isNull(root.get("parent")), // Only delete root items in trash
                        builder.equal(root.get("user").get("id"), userId),
                        builder.isNotNull(root.get("deletedAt"))))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Item not found or does not belong to current user."));
    User user = fsNode.getUser();
    // 3. ---- Delete file metadata if exists ----
    if (fsNode.getFileMetadata() != null) {
      fileMetadataRepository.delete(fsNode.getFileMetadata());
      String blobKey = fsNode.getFileMetadata().getBlobKey();
      String path = generateBlobPath(blobKey);
      storageService.deleteFile(path);
      //  4. ---- Update usage ----
      user.setTotalUsage(user.getTotalUsage() - fsNode.getSize());
    }
    // 5. ---- Delete item from repository ----
    repository.delete(fsNode);

    // 6. ---- Check if item has sub-nodes and delete them permanently ----
    List<FSNode> subNodes =
        repository.findAll(
            (root, _, builder) -> {
              Expression<Integer> pos =
                  builder.function(
                      "array_position",
                      Integer.class,
                      root.get("ancestor"),
                      builder.literal(fsNode.getId()));
              return builder.and(
                  builder.greaterThan(pos, 0), builder.isNotNull(root.get("deletedAt")));
            });

    // 7. ---- Delete sub-nodes and their file metadata if exists ----
    List<FileMetadata> metadataList = new ArrayList<>();
    for (FSNode subNode : subNodes) {
      // Delete file metadata if exists
      if (subNode.getFileMetadata() != null) {
        fileMetadataRepository.delete(subNode.getFileMetadata());
        String blobKey = subNode.getFileMetadata().getBlobKey();
        String path = generateBlobPath(blobKey);
        storageService.deleteFile(path);
        // 8. ---- Update usage ----
        user.setTotalUsage(user.getTotalUsage() - subNode.getSize());
        metadataList.add(subNode.getFileMetadata());
      }
    }
    // 9. ---- Delete sub-nodes from repository ----
    repository.deleteAll(subNodes);
    fileMetadataRepository.deleteAll(metadataList);
    userRepository.save(user);
  }

  // ============================ HELPER METHODS ============================
  /**
   * This method retrieves all file system nodes under the file system node. if parentId is null, it
   * retrieves all items in the root folder.
   *
   * @param parentId The ID of the parent folder. If null, it retrieves nodes at the root level.
   */
  private List<FSNode> getItemInNode(Long parentId) {
    // 2. ---- Build basic spec ----
    Specification<FSNode> spec =
        (root, _, builder) -> builder.and(builder.isNull(root.get("deletedAt")));
    // 3. ---- Add parentId condition ----
    if (parentId != null) {
      spec = spec.and((root, _, builder) -> builder.equal(root.get("parent").get("id"), parentId));
    } else {
      spec = spec.and((root, _, builder) -> builder.isNull(root.get("parent")));
    }
    return repository.findAll(spec);
  }

  private FSNode getItemById(Long id) {
    return repository
        .findOne(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), id), builder.isNull(root.get("deletedAt"))))
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Item not found or does not belong to current user."));
  }

  /** This method get the parent folder if it exists. */
  private FSNode getParentFolder(Long parentId) {
    FSNode result =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), parentId),
                        builder.equal(root.get("type"), FSType.FOLDER),
                        builder.isNull(root.get("deletedAt"))))
            .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found."));
    return result;
  }

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

  private FSNode moveItem(FSNode sourceNode, FSNode destinationNode) {
    if (sourceNode.getParent() == destinationNode) {
      return sourceNode; // No need to move if the source is already in the destination
    }

    // 1. ---- Check if parent is child of fsNode ----
    if (destinationNode != null && destinationNode.getAncestor().contains(sourceNode.getId())) {
      throw new ApiException(ErrorCode.CYCLIC_FILE_DETECTED);
    }

    // 2. ---- Update fsNode's parent and ancestor ----
    sourceNode.setParent(destinationNode);
    List<Long> newAncestor = new ArrayList<>();
    if (destinationNode != null) {
      newAncestor.addAll(destinationNode.getAncestor());
      newAncestor.add(destinationNode.getId());
    }
    sourceNode.setAncestor(newAncestor);
    sourceNode.setLastAccessed(Instant.now());
    sourceNode = repository.save(sourceNode);

    // 3. ---- Update all sub-nodes' ancestor ----
    Long fsNodeId = sourceNode.getId();
    List<FSNode> subNodes = getSubNodes(fsNodeId);
    subNodes.stream()
        .forEach(
            node -> {
              int index = node.getAncestor().indexOf(fsNodeId);
              node.getAncestor().subList(0, index + 1).clear();
              node.getAncestor().addAll(0, newAncestor);
            });
    repository.saveAll(subNodes);
    return sourceNode;
  }

  private FSNode copy(FSNode sourceNode, FSNode destinationNode) {
    if (sourceNode.getParent() == destinationNode) {
      return sourceNode; // No need to copy if the source is already in the destination
    }
    // 1. ---- Clone basic info ----
    FSNode copiedRoot = shallowCopyFSNode(sourceNode);
    copiedRoot.setParent(destinationNode);

    // 1.1 ---- Rebuild ancestor ----
    List<Long> newAncestor = new ArrayList<>();
    if (destinationNode != null) {
      newAncestor.addAll(destinationNode.getAncestor());
      newAncestor.add(destinationNode.getId());
    }
    copiedRoot.setAncestor(newAncestor);
    copiedRoot.setLastAccessed(Instant.now());
    copiedRoot = repository.save(copiedRoot);

    // 2. ---- Clone fileMetadata ----
    if (sourceNode.getFileMetadata() != null) {
      FileMetadata metadata = cloneFileMetadata(sourceNode.getFileMetadata());
      metadata.setFile(sourceNode);
      fileMetadataRepository.save(metadata);
    }

    // 3. ---- Get all sub-nodes in source node ----
    List<FSNode> originalSubNodes = getSubNodes(sourceNode.getId());

    // 4. ---- Data struct for copy ----
    // old id -> new id mapping
    Map<Long, Long> oldToNewIdMapping = new HashMap<>();
    Map<Long, Long> newToOldIdMapping = new HashMap<>();

    // copiedNodeMap
    Map<Long, FSNode> copiedNodeMap = new HashMap<>();
    Map<Long, FSNode> originalNodeMap = new HashMap<>();

    oldToNewIdMapping.put(sourceNode.getId(), copiedRoot.getId());
    newToOldIdMapping.put(copiedRoot.getId(), sourceNode.getId());
    copiedNodeMap.put(copiedRoot.getId(), copiedRoot);
    originalNodeMap.put(sourceNode.getId(), sourceNode);

    // List for copied nodes and metadata
    List<FSNode> copiedSubNodes = new ArrayList<>();
    List<FileMetadata> copiedMetadataList = new ArrayList<>();

    for (FSNode originalNode : originalSubNodes) {
      // Clone sub-node basic info
      FSNode copiedSubNode = shallowCopyFSNode(originalNode);
      copiedSubNode = repository.save(copiedSubNode);

      //  map tracking
      oldToNewIdMapping.put(originalNode.getId(), copiedSubNode.getId());
      copiedNodeMap.put(copiedSubNode.getId(), copiedSubNode);
      copiedSubNodes.add(copiedSubNode);

      // Clone file metadata if exists
      if (originalNode.getFileMetadata() != null) {
        FileMetadata copiedMetadata = cloneFileMetadata(originalNode.getFileMetadata());
        copiedMetadata.setFile(copiedSubNode);
        copiedMetadataList.add(copiedMetadata);
      }
    }

    // 5. ---- Rebuild ancestor for sub-nodes ----
    for (FSNode copiedSubNode : copiedSubNodes) {
      // Build ancestor
      FSNode oldNode = originalNodeMap.get(newToOldIdMapping.get(copiedSubNode.getId()));
      copiedSubNode.setParent(
          copiedNodeMap.get(oldToNewIdMapping.get(oldNode.getParent().getId())));
      List<Long> newSubAncestor = new ArrayList<>(oldNode.getAncestor());
      int index = newSubAncestor.indexOf(sourceNode.getId());
      if (index != -1) {
        newSubAncestor.set(index, destinationNode.getId());
        newSubAncestor.subList(0, index).clear();
        newSubAncestor.addAll(0, newAncestor);
      }
      for (int i = index + 1; i < newSubAncestor.size(); i++) {
        Long newId = oldToNewIdMapping.get(newSubAncestor.get(i));
        if (newId == null) {
          throw new ApiException(
              ErrorCode.DATA_INTEGRITY_VIOLATION,
              "Ancestor ID " + newSubAncestor.get(i) + " not found in copied nodes.");
        }
        newSubAncestor.set(i, newId);
      }
      copiedSubNode.setAncestor(newSubAncestor);
      // Post check ancestor
      if (copiedSubNode.getAncestor().getLast() != copiedSubNode.getParent().getId()) {
        throw new ApiException(
            ErrorCode.DATA_INTEGRITY_VIOLATION,
            "Ancestor of copied sub-node does not match its parent.");
      }
    }

    // 4. ---- Save all copied nodes and metadata ----
    repository.saveAll(copiedSubNodes);
    fileMetadataRepository.saveAll(copiedMetadataList);

    return copiedRoot;
  }

  private FileMetadata cloneFileMetadata(FileMetadata metadata) {
    FileMetadata copiedFileMetadata = new FileMetadata();
    copiedFileMetadata.setMimeType(metadata.getMimeType());
    copiedFileMetadata.setExtension(metadata.getExtension());
    String blobKey = UUID.randomUUID().toString();
    copiedFileMetadata.setBlobKey(blobKey);
    String oldPath = generateBlobPath(metadata.getBlobKey());
    String newPath = generateBlobPath(blobKey);
    storageService.copyFile(oldPath, newPath);
    return copiedFileMetadata;
  }

  private FSNode shallowCopyFSNode(FSNode fsNode) {
    FSNode copiedNode = new FSNode();
    copiedNode.setName(fsNode.getName());
    copiedNode.setType(fsNode.getType());
    copiedNode.setSize(fsNode.getSize());
    copiedNode.setUser(
        userRepository
            .findById(securityUtils.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    copiedNode.setHidden(fsNode.isHidden());
    copiedNode.setLocked(fsNode.isLocked());
    copiedNode.setLastAccessed(Instant.now());
    return repository.save(copiedNode);
  }

  private List<FSNode> getSubNodes(Long fsNodeId) {
    if (fsNodeId == null) {
      throw new DataConflictException("fsNodeId cannot be null for sub-node retrieval.");
    }
    return repository.findAll(
        (root, _, builder) -> {
          Expression<Integer> pos =
              builder.function(
                  "array_position", Integer.class, root.get("ancestor"), builder.literal(fsNodeId));
          return builder.and(builder.greaterThan(pos, 0), builder.isNull(root.get("deletedAt")));
        });
  }
}
