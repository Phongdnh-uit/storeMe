package com.DPhong.storeMe.service.tag;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.tag.TagRequestDTO;
import com.DPhong.storeMe.dto.tag.TagResponseDTO;
import com.DPhong.storeMe.entity.fsNode.FSNode;
import com.DPhong.storeMe.entity.tag.FSNodeTag;
import com.DPhong.storeMe.entity.tag.Tag;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.TagMapper;
import com.DPhong.storeMe.repository.FSNodeRepository;
import com.DPhong.storeMe.repository.FSNodeTagRepository;
import com.DPhong.storeMe.repository.TagRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.GenericService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends GenericService<Tag, TagRequestDTO, TagResponseDTO>
    implements TagService {

  private final FSNodeRepository fsNodeRepository;
  private final FSNodeTagRepository fsNodeTagRepository;

  public TagServiceImpl(
      TagRepository repository,
      TagMapper mapper,
      FSNodeTagRepository fsNodeTagRepository,
      FSNodeRepository fsNodeRepository) {
    super(repository, mapper);
    this.fsNodeTagRepository = fsNodeTagRepository;
    this.fsNodeRepository = fsNodeRepository;
  }

  // ============================ FIND ALL ============================
  @Override
  public PageResponse<TagResponseDTO> findAll(Specification<Tag> specification, Pageable pageable) {
    Specification<Tag> ownerSpec =
        (root, _, builder) -> builder.equal(root.get("ownerId"), SecurityUtils.getCurrentUserId());
    ownerSpec = ownerSpec.and(specification);
    return super.findAll(ownerSpec, pageable);
  }

  // ============================ FIND BY ID ============================
  @Override
  public TagResponseDTO findById(Long id) {
    Specification<Tag> ownerSpec =
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("id"), id),
                builder.equal(root.get("ownerId"), SecurityUtils.getCurrentUserId()));
    Tag entity =
        repository
            .findOne(ownerSpec)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    return mapper.entityToResponse(entity);
  }

  // ============================ CREATE ============================
  @Override
  protected void afterCreateMapper(TagRequestDTO request, Tag entity) {
    entity.setOwnerId(SecurityUtils.getCurrentUserId());
  }

  // ============================ UPDATE ============================
  @Override
  protected void beforeUpdateMapper(Long id, TagRequestDTO request, Tag oldEntity) {
    Long userId = SecurityUtils.getCurrentUserId();
    if (!oldEntity.getOwnerId().equals(userId)) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to update this tag");
    }
  }

  // ============================ TAG ASSIGNMENT ============================
  @Override
  public void assignTagToFSNode(Long tagId, Long fsNodeId) {
    boolean isTagExists =
        repository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), tagId),
                    builder.equal(root.get("ownerId"), SecurityUtils.getCurrentUserId())));
    if (!isTagExists) {
      throw new ResourceNotFoundException("Tag not found");
    }
    FSNode fsNode =
        fsNodeRepository
            .findById(fsNodeId)
            .orElseThrow(() -> new ResourceNotFoundException("FSNode not found"));
    if (fsNode.getUser().getId() != SecurityUtils.getCurrentUserId()) {
      throw new ApiException(ErrorCode.ACCESS_DENIED, "You do not have permission to assign tag");
    }
    boolean isAlreadyAssigned =
        fsNodeTagRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("tagId"), tagId),
                    builder.equal(root.get("fsNodeId"), fsNodeId)));
    if (isAlreadyAssigned) {
      throw new ApiException(
          ErrorCode.DATA_INTEGRITY_VIOLATION, "Tag is already assigned to FSNode");
    }
    FSNodeTag fsNodeTag = new FSNodeTag();
    fsNodeTag.setTagId(tagId);
    fsNodeTag.setFsNodeId(fsNodeId);
    fsNodeTagRepository.save(fsNodeTag);
  }

  // ============================ TAG REMOVAL ============================
  @Override
  public void removeTagFromFSNode(Long tagId, Long fsNodeId) {
    boolean isTagExists =
        repository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), tagId),
                    builder.equal(root.get("ownerId"), SecurityUtils.getCurrentUserId())));
    if (!isTagExists) {
      throw new ResourceNotFoundException("Tag not found");
    }
    FSNode fsNode =
        fsNodeRepository
            .findById(fsNodeId)
            .orElseThrow(() -> new ResourceNotFoundException("FSNode not found"));
    if (fsNode.getUser().getId() != SecurityUtils.getCurrentUserId()) {
      throw new ApiException(ErrorCode.ACCESS_DENIED, "You do not have permission to remove tag");
    }
    FSNodeTag fsNodeTag =
        fsNodeTagRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("tagId"), tagId),
                        builder.equal(root.get("fsNodeId"), fsNodeId)))
            .orElseThrow(() -> new ResourceNotFoundException("Tag is not assigned to FSNode"));
    fsNodeTagRepository.delete(fsNodeTag);
  }
}
