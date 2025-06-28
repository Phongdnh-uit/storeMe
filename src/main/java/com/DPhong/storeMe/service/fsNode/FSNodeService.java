package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UploadFileRequestDTO;
import com.DPhong.storeMe.entity.FSNode;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface FSNodeService {

  PageResponse<FSResponseDTO> getAll(Long parentId, Specification<FSNode> spec, Pageable pageable);

  FSResponseDTO getById(Long id);

  FSResponseDTO createFolder(CreateFolderRequestDTO request);

  List<FSResponseDTO> uploadFiles(UploadFileRequestDTO request);

  Resource getFile(Long id);

  FSResponseDTO update(Long id, UpdateFSNodeRequestDTO request);

  void delete(Long id);

  void deleteMany(List<Long> ids);

  PageResponse<FSResponseDTO> getTrash(Specification<FSNode> spec, Pageable pageable);

  void restore(Long id);
}
