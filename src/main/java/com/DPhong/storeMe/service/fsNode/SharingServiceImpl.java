package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.fileSystemNode.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateSharingRequestDTO;
import com.DPhong.storeMe.entity.SharingPermission;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.repository.FSNodeRepository;
import com.DPhong.storeMe.repository.SharingPermissionRepository;
import com.DPhong.storeMe.repository.SharingRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SharingServiceImpl implements SharingService {

  private final FSNodeRepository fsNodeRepository;
  private final SharingRepository sharingRepository;
  private final SharingPermissionRepository sharingPermissionRepository;
  private final SecurityUtils securityUtils;

  @Override
  public void create(Long fsNodeId, CreateSharingRequestDTO createSharingRequestDTO) {
    throw new ApiException(ErrorCode.INTERNAL_ERROR, "Sharing creation is not implemented yet.");
  }

  @Transactional
  @Override
  public void update(Long id, UpdateSharingRequestDTO updateSharingRequestDTO) {
    boolean isValid =
        sharingRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("id"), id),
                    builder.equal(
                        root.get("grantedBy").get("id"), securityUtils.getCurrentUserId())));
    if (!isValid) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You are not allowed to update this sharing.");
    }
    sharingPermissionRepository.delete(
        (root, _, builder) -> builder.and(builder.equal(root.get("sharingId"), id)));
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
