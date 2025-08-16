package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.entity.FSNode;
import com.DPhong.storeMe.entity.Sharing;
import com.DPhong.storeMe.entity.SharingPermission;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.SharingType;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.repository.SharingPermissionRepository;
import com.DPhong.storeMe.repository.SharingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FSPermissionServiceImpl implements FSPermissionService {
  private final SharingRepository sharingRepository;
  private final SharingPermissionRepository sharingPermissionRepository;

  // ============================ CHECK USER CAN READ ============================
  @Override
  public void checkCanRead(Long userId, FSNode fsNodeId) {
    if (fsNodeId.getUser().getId() == userId) {
      return;
    }
    if (isLocked(fsNodeId)) {
      throw new ApiException(ErrorCode.FSNODE_LOCKED);
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    if (sharing.isEmpty()) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to read this file");
    }
    List<SharingPermission> sharingPermissions = getSharingPermission(sharing.get().getId());
    boolean canRead =
        sharingPermissions.stream()
            .anyMatch(
                v ->
                    v.getSharingType() == SharingType.OWNER
                        || v.getSharingType() == SharingType.READ);
    if (!canRead) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to read this file");
    }
  }

  // ============================ CHECK USER CAN WRITE ============================
  @Override
  public void checkCanWrite(Long userId, FSNode fsNodeId) {
    if (fsNodeId.getUser().getId() == userId) {
      return; // Owner can always write
    }
    if (isLocked(fsNodeId)) {
      throw new ApiException(ErrorCode.FSNODE_LOCKED);
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    if (sharing.isEmpty()) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to write to this file");
    }
    List<SharingPermission> sharingPermissions = getSharingPermission(sharing.get().getId());
    boolean canWrite =
        sharingPermissions.stream()
            .anyMatch(
                v ->
                    v.getSharingType() == SharingType.OWNER
                        || v.getSharingType() == SharingType.WRITE);
    if (!canWrite) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to write to this file");
    }
  }

  // ============================ CHECK USER CAN DELETE ============================
  @Override
  public void checkCanDelete(Long userId, FSNode fsNodeId) {
    if (fsNodeId.getUser().getId() == userId) {
      return; // Owner can always delete
    }
    if (isLocked(fsNodeId)) {
      throw new ApiException(ErrorCode.FSNODE_LOCKED);
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    if (sharing.isEmpty()) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to delete this file");
    }
    List<SharingPermission> sharingPermissions = getSharingPermission(sharing.get().getId());
    boolean canDelete =
        sharingPermissions.stream()
            .anyMatch(
                v ->
                    v.getSharingType() == SharingType.OWNER
                        || v.getSharingType() == SharingType.DELETE);
    if (!canDelete) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to delete this file");
    }
  }

  // ============================ HELPER METHOD ============================

  boolean isLocked(FSNode fsNode) {
    return fsNode.isLocked();
  }

  List<SharingPermission> getSharingPermission(Long sharingId) {
    return sharingPermissionRepository.findAll(
        (root, _, builder) -> builder.and(builder.equal(root.get("sharingId"), sharingId)));
  }

  Optional<Sharing> getSharedIfExists(Long userId, FSNode fsNode) {
    List<Long> ancestor = new ArrayList<>(fsNode.getAncestor());
    ancestor.add(fsNode.getId()); // Include the current node itself
    return sharingRepository.findOne(
        (root, _, builder) ->
            builder.and(
                root.get("sharedFSNode").in(ancestor),
                builder.equal(root.get("grantedTo"), userId)));
  }
}
