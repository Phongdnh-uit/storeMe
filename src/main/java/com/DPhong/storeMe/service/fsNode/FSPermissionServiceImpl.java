package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.entity.FSNode;
import com.DPhong.storeMe.entity.Sharing;
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

  @Override
  public void checkCanRead(Long userId, FSNode fsNodeId) {
    if (isOwner(userId, fsNodeId)) {
      return; // Owner can always read
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    boolean canRead =
        sharingPermissionRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("sharingId"), sharing.get().getId()),
                    builder.equal(root.get("sharingType"), SharingType.READ)));
    if (!canRead) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to read this file");
    }
  }

  @Override
  public void checkCanWrite(Long userId, FSNode fsNodeId) {
    if (isOwner(userId, fsNodeId)) {
      return; // Owner can always write
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    boolean canWrite =
        sharingPermissionRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("sharingId"), sharing.get().getId()),
                    builder.equal(root.get("sharingType"), SharingType.WRITE)));
    if (!canWrite) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to write to this file");
    }
  }

  @Override
  public void checkCanDelete(Long userId, FSNode fsNodeId) {
    if (isOwner(userId, fsNodeId)) {
      return; // Owner can always delete
    }
    Optional<Sharing> sharing = getSharedIfExists(userId, fsNodeId);
    boolean canDelete =
        sharingPermissionRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("sharingId"), sharing.get().getId()),
                    builder.equal(root.get("sharingType"), SharingType.DELETE)));
    if (!canDelete) {
      throw new ApiException(
          ErrorCode.ACCESS_DENIED, "You do not have permission to delete this file");
    }
  }

  boolean isOwner(Long userId, FSNode fsNode) {
    return fsNode.getUser().getId().equals(userId);
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
