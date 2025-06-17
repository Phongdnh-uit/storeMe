package com.DPhong.storeMe.service.folder;

import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.enums.FolderAction;
import com.DPhong.storeMe.service.CrudService;

public interface FolderService
    extends CrudService<Folder, Long, FolderRequestDTO, FolderResponseDTO> {

  FolderResponseDTO update(Long id, FolderRequestDTO request, FolderAction action);

  void cleanTrash();

  void cronJobCleanTrash();
}
