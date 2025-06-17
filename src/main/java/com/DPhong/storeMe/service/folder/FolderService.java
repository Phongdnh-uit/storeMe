package com.DPhong.storeMe.service.folder;

import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.service.CrudService;

public interface FolderService
    extends CrudService<Folder, Long, FolderRequestDTO, FolderResponseDTO> {

  FolderResponseDTO update(Long id, UpdateFSNodeRequestDTO updateFSNodeRequestDTO);

  void cleanTrash();

  void cronJobCleanTrash();
}
