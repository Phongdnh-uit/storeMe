package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.sharing.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.sharing.SharingResponseDTO;
import com.DPhong.storeMe.dto.sharing.UpdateSharingRequestDTO;
import com.DPhong.storeMe.entity.fsNode.FSNode;
import com.DPhong.storeMe.entity.sharing.Sharing;
import com.DPhong.storeMe.entity.sharing.SharingPermission;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.SharingType;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.mapper.SharingMapper;
import com.DPhong.storeMe.repository.FSNodeRepository;
import com.DPhong.storeMe.repository.SharingPermissionRepository;
import com.DPhong.storeMe.repository.SharingRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SharingServiceImpl implements SharingService {

  private final FSNodeRepository fsNodeRepository;
  private final UserRepository userRepository;
  private final SharingRepository sharingRepository;
  private final SharingPermissionRepository sharingPermissionRepository;
  private final SecurityUtils securityUtils;
  private final SharingMapper sharingMapper;

  @Override
  public PageResponse<SharingResponseDTO> getAllSharedByMe(
      Specification<Sharing> spec, Pageable pageable) {
    Long currentUserId = securityUtils.getCurrentUserId();
    Specification<Sharing> finalSpec =
        (root, _, builder) -> builder.and(builder.equal(root.get("grantedBy"), currentUserId));
    finalSpec = finalSpec.and(spec);

    Page<Sharing> sharingPage = sharingRepository.findAll(finalSpec, pageable);
    return PageResponse.from(sharingPage.map(sharingMapper::entityToResponse));
  }

  @Override
  public PageResponse<SharingResponseDTO> getAllSharedWithMe(
      Specification<Sharing> spec, Pageable pageable) {
    Long currentUserId = securityUtils.getCurrentUserId();
    Specification<Sharing> finalSpec =
        (root, _, builder) -> builder.and(builder.equal(root.get("grantedTo"), currentUserId));
    finalSpec = finalSpec.and(spec);
    Page<Sharing> sharingPage = sharingRepository.findAll(finalSpec, pageable);
    return PageResponse.from(sharingPage.map(sharingMapper::entityToResponse));
  }

  @Transactional
  @Override
  public SharingResponseDTO create(Long fsNodeId, CreateSharingRequestDTO createSharingRequestDTO) {
    Long currentUserId = securityUtils.getCurrentUserId();
    // 1. ---- Validate ----
    if (!userRepository.existsById(createSharingRequestDTO.getGrantedTo())) {
      throw new ApiException(
          ErrorCode.VALIDATION_FAILED,
          List.of(FieldError.from("grantedTo", "User does not exist.")));
    }
    FSNode fsNode =
        fsNodeRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), fsNodeId),
                        builder.equal(root.get("user").get("id"), currentUserId),
                        builder.isNull(root.get("deletedAt"))))
            .orElseThrow(
                () ->
                    new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "File system node not found."));
    boolean isShared =
        sharingRepository.exists(
            (root, _, builder) ->
                builder.and(
                    root.get("sharedFSNode").in(fsNode.getAncestor()),
                    builder.equal(root.get("grantedBy"), currentUserId),
                    builder.equal(root.get("grantedTo"), createSharingRequestDTO.getGrantedTo())));
    if (isShared) {
      throw new ApiException(
          ErrorCode.VALIDATION_FAILED,
          List.of(FieldError.from("grantedTo", "This user already has access to this fsNode.")));
    }

    // 2. ---- Create Sharing ----
    Sharing sharing = new Sharing();
    sharing.setGrantedBy(currentUserId);
    sharing.setGrantedTo(createSharingRequestDTO.getGrantedTo());
    sharing.setSharedFSNode(fsNodeId);
    sharing = sharingRepository.save(sharing);

    // 3. ---- Create Sharing Permissions ----
    List<SharingPermission> permissions = new ArrayList<>();
    for (SharingType permission : createSharingRequestDTO.getPermissions()) {
      SharingPermission sharingPermission = new SharingPermission();
      sharingPermission.setSharingId(sharing.getId());
      sharingPermission.setSharingType(permission);
      permissions.add(sharingPermission);
    }
    sharingPermissionRepository.saveAll(permissions);

    // 4. ---- Prepare Response ----
    SharingResponseDTO response = sharingMapper.entityToResponse(sharing);
    response.setPermissions(createSharingRequestDTO.getPermissions());
    return response;
  }

  @Transactional
  @Override
  public SharingResponseDTO update(Long id, UpdateSharingRequestDTO updateSharingRequestDTO) {
    // 1. ---- Validate ----
    Sharing sharing =
        sharingRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.equal(
                            root.get("grantedBy").get("id"), securityUtils.getCurrentUserId())))
            .orElseThrow(
                () ->
                    new ApiException(
                        ErrorCode.ACCESS_DENIED,
                        "Sharing not found or you are not allowed to update it."));

    // 2. ---- Delete old permissions ----
    sharingPermissionRepository.delete(
        (root, _, builder) -> builder.and(builder.equal(root.get("sharingId"), id)));

    // 3. ---- Create new permissions ----
    Instant now = Instant.now();
    List<SharingPermission> sharingPermission =
        updateSharingRequestDTO.getPermissions().stream()
            .map(
                permission -> {
                  SharingPermission entity = new SharingPermission();
                  entity.setSharingId(id);
                  entity.setSharingType(permission);
                  return entity;
                })
            .toList();
    sharingPermissionRepository.saveAll(sharingPermission);
    sharing.setUpdatedAt(now);
    sharingRepository.updateUpdatedAt(id, now);
    SharingResponseDTO response = sharingMapper.entityToResponse(sharing);
    response.setPermissions(updateSharingRequestDTO.getPermissions());
    return response;
  }

  @Override
  public void delete(Long id) {
    boolean isValid =
        sharingRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), id),
                    builder.equal(
                        root.get("grantedBy").get("id"), securityUtils.getCurrentUserId())));
    if (!isValid) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "Sharing not found or you are not allowed to delete it.");
    }
    sharingRepository.deleteById(id);
  }
}
