package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.fileSystemNode.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateSharingRequestDTO;

public interface SharingService {
  void create(Long fsNodeId, CreateSharingRequestDTO createSharingRequestDTO);

  void update(Long id, UpdateSharingRequestDTO updateSharingRequestDTO);

  void delete(Long id);
}
